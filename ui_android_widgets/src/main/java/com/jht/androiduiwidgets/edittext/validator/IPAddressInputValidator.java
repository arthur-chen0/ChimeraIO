package com.jht.androiduiwidgets.edittext.validator;

public class IPAddressInputValidator implements IUserInputValidator {


    public IPAddressInputValidator() {

    }

    @Override
    public InputValidationResult validate(String value, String previousValue) {
        String[] ipParts = value.split("\\.");
        if (ipParts.length != 4) {
            return new InputValidationResult(InputValidationResult.RESULT.IP_ADDRESS_ERROR, "0.0.0.0", value);
        }
        for (int i = 0; i < 4; i++) {
            try {
                int ipPart = Integer.parseInt(ipParts[i]);
                if (ipPart < 0 || ipPart > 255) {
                    return new InputValidationResult(InputValidationResult.RESULT.IP_ADDRESS_ERROR, "0.0.0.0", value);
                }
            } catch (Exception ex) {
                return new InputValidationResult(InputValidationResult.RESULT.IP_ADDRESS_ERROR, "0.0.0.0", value);
            }
        }
        return new InputValidationResult(InputValidationResult.RESULT.VALID, null, value);
    }
}
