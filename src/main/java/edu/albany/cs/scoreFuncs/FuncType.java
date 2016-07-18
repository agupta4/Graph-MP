package edu.albany.cs.scoreFuncs;


/**
 * Score functions that we have now.
 *
 * @author baojian
 */
public enum FuncType {

    KulldorffStat,
    EBP,
    EMS,
    ProtestScanStat,
    Unknown;

    public static FuncType defaultFuncType() {
        return FuncType.Unknown;
    }
}
