package cz.ssc.dapro;

public enum Errors {
    // error codes defined by OAI-PMH
    noRecordsMatch,
    noSetHierarchy,
    cannotDisseminateFormat,
    badArgument,
    idDoesNotExist,
    badResumptionToken,
    badVerb,
    // specific error messages for error codes above
    extraArgument,
    illegalArgumentValue,
    sourceFileNotFound;

    public static Errors getCode(Errors error) {
        switch (error) {
            case extraArgument:
            case illegalArgumentValue:
                return Errors.badArgument;

            case sourceFileNotFound:
                return Errors.badResumptionToken;

            default:
                return error;
        }
    }
}
