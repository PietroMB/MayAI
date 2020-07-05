package com.hacktim.cibotto.service;

import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.dialogflow.v2.model.*;
import com.hacktim.cibotto.repository.ProdottiDao;
import com.hacktim.cibotto.repository.ProdottiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.StringWriter;
import java.util.*;

import static com.hacktim.cibotto.Constants.*;

@Service
public class WebhookService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();

    @Autowired
    private ProdottiRepository prodottiRepository;

    @Value("${cibotto.www}")
    private String cibottoWww;
    @Value("${cibotto.sms.uri}")
    private String cibottoSmsUri;
    @Value("${cibotto.sms.address}")
    private String cibottoSmsAddress;
    @Value("${cibotto.sms.message}")
    private String cibottoSmsMessage;

    public String elabCb (String rawRequest) {
        String jsonResponse = "{}";
        try {
            GoogleCloudDialogflowV2WebhookRequest request = jacksonFactory.createJsonParser(rawRequest)
                .parse(GoogleCloudDialogflowV2WebhookRequest.class);

            StringWriter stringWriter = new StringWriter();
            JsonGenerator jsonGenerator = jacksonFactory.createJsonGenerator(stringWriter);
            GoogleCloudDialogflowV2WebhookResponse webhookResponse = new GoogleCloudDialogflowV2WebhookResponse();

            String intentDisplayName = request.getQueryResult().getIntent().getDisplayName();
            String languageCode = request.getQueryResult().getLanguageCode();
            Map<String, Object> parameterMap = request.getQueryResult().getParameters();

            String fulfillmentText = "non riesco a rispondere"; //  TODO lookup with languageCode

            String imageProdUri = null;
            String imageLocUri = null;
            ProdottiDao prodottiDao = null;
            switch (intentDisplayName) {
                case INTENT_ADVISE:
                    String typ = parameterMap.get(ENTITY_ADVICE_TYPE).toString();
                    prodottiDao = getProdottoByTyp(typ, languageCode);

                    break;
                case INTENT_LOCATION:
                    String prod = parameterMap.get(ENTITY_LOCATION_PROD).toString();
                    prodottiDao = getProdottoByProd(prod, languageCode);
                    break;
                case INTENT_HELP :
                    String name = parameterMap.get(ENTITY_HELP_NAME).toString();
                    sendSms(name+" "+cibottoSmsMessage + " " + "elettrodomestici"); //  TODO parametrare in base a iid
                    fulfillmentText="L'addetto al reparto ti raggiungerà qui tra pochissimo, "+name+"!";
                    break;
                default :
                    break;
            }
            if (prodottiDao != null) {
                fulfillmentText = "Il prodotto " + prodottiDao.getProd() + " si trova " + prodottiDao.getLoc();   //  TODO lookup with languageCode
                if (prodottiDao.getProdImg() != null) {
                    imageProdUri = cibottoWww+prodottiDao.getProdImg();
                }
                if (prodottiDao.getLocImg() != null) {
                    imageLocUri = cibottoWww+prodottiDao.getLocImg();
                }
            }
            webhookResponse.setFulfillmentText(fulfillmentText);
            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put(PAYLOAD_IMAGE_PROD_URI, imageProdUri);
            payloadMap.put(PAYLOAD_IMAGE_LOC_URI, imageLocUri);
            webhookResponse.setPayload(payloadMap);

            jsonGenerator.serialize(webhookResponse);
            jsonGenerator.flush();
            jsonResponse = stringWriter.toString();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return jsonResponse;
    }

    //  TODO code refactoring
    public ProdottiDao getProdottoByTyp(String typ, String languageCode) {
        ProdottiDao prodottiDao = null;
        if (typ != null && !typ.isEmpty()) {
            String typKey = typ.replaceAll("[^a-zA-Z0-9]","");    //  TODO lookup with languageCode : match db key
            List<ProdottiDao> prodottiList = prodottiRepository.findByTypOrderByQty(typKey);
            if (prodottiList != null && !prodottiList.isEmpty()) {
                prodottiDao = prodottiList.get(0);
            } else {
                logger.warn("Missing typ " + typ+ " language "+languageCode);
            }
        }
        return prodottiDao;
    }

    public ProdottiDao getProdottoByProd(String prod, String languageCode) {
        ProdottiDao prodottiDao = null;
        if (prod != null && !prod.isEmpty()) {
            String prodKey = prod.replaceAll("[^a-zA-Z0-9]","");    //  TODO lookup with languageCode : match db key
            List<ProdottiDao> prodottiList = prodottiRepository.findByProdOrderByQty(prodKey);
            if (prodottiList != null && !prodottiList.isEmpty()) {
                prodottiDao = prodottiList.get(0);
            } else {
                logger.warn("Missing prod " + prod+ " language "+languageCode);
            }
        }
        return prodottiDao;
    }

    public String sendSms(String message)  {
        String result;
        String apikey;
        if (cibottoSmsUri.isEmpty()) {
            result = "SmsUri empty";
        } else {
            apikey = System.getenv(ENV_TIM_APIKEY);
            if (apikey == null || apikey.isEmpty()) {
                result = ENV_TIM_APIKEY+" empty";
            } else {
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.set("apikey", apikey);
                    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
                    map.add("address", cibottoSmsAddress);
                    map.add("message", message);
                    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
                    ResponseEntity<String> response = new RestTemplate().postForEntity(cibottoSmsUri, request, String.class);
                    result = response.getStatusCode()+" "+ response.getBody();
                } catch (Exception e) {
                    result = e.getMessage();
                }
            }
        }
        return result;
    }

}
