package org.example;

import com.gitb.core.AnyContent;
import com.gitb.core.ValueEmbeddingEnumeration;
import com.gitb.tr.BAR;
import com.gitb.vs.ValidateRequest;
import com.gitb.vs.ValidationResponse;
import com.gitb.vs.ValidationService_Service;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Application entry point that is also used to trigger validation.
 */
public class Application {

    public static void main(String[] args) throws MalformedURLException {
        new Application().doValidation();
    }

    private void doValidation() throws MalformedURLException {
        // 1. Prepare the input
        ValidateRequest request = new ValidateRequest();

        AnyContent contentToValidateInput = new AnyContent();
        contentToValidateInput.setName("contentToValidate");
        contentToValidateInput.setValue("https://www.itb.ec.europa.eu/files/json/sample.json");
        contentToValidateInput.setEmbeddingMethod(ValueEmbeddingEnumeration.URI);
        request.getInput().add(contentToValidateInput);

        AnyContent schemaContent = new AnyContent();
        schemaContent.setName("schema");
        schemaContent.setEmbeddingMethod(ValueEmbeddingEnumeration.URI);
        schemaContent.setValue("https://www.itb.ec.europa.eu/files/json/PurchaseOrder-large.schema.json");
        AnyContent schemasInput = new AnyContent();
        schemasInput.setName("externalSchemas");
        schemasInput.getItem().add(schemaContent);
        request.getInput().add(schemasInput);

        // 2. Call the service endpoint
        ValidationService_Service client = new ValidationService_Service(new URL("https://www.itb.ec.europa.eu/json/soap/any/validation?wsdl"));
        ValidationResponse response = client.getValidationServicePort().validate(request);

        // 3. Process the response.
        System.out.printf("Errors %s, warnings %s, messages %s%n",
                response.getReport().getCounters().getNrOfErrors(),
                response.getReport().getCounters().getNrOfWarnings(),
                response.getReport().getCounters().getNrOfAssertions()
        );
        response.getReport().getReports().getInfoOrWarningOrError().forEach((reportItem) -> {
            System.out.printf("Level [%s], Description [%s]%n", reportItem.getName().getLocalPart(), ((BAR)reportItem.getValue()).getDescription());
        });
    }

}
