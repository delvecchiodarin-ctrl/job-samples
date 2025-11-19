package gov.epa.eis.batch.xmlsnapshot.xml;

import gov.epa.eis.model.code.CodeTable;

/**
 * XMarshallerUtility
 */
public class XMarshallerUtility {
    public static String getCode(CodeTable<String> codeTable) {
        if (codeTable == null)
            return null;
        return codeTable.getCode();
    }
}