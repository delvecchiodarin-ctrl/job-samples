package gov.epa.eis.batch.xmlsnapshot.xml;

/**
 * XMarshaller
 */
public interface XMarshaller<X, M> {
    public X marshall(M model);
}

