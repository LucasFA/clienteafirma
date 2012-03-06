/* Copyright (C) 2011 [Gobierno de Espana]
 * This file is part of "Cliente @Firma".
 * "Cliente @Firma" is free software; you can redistribute it and/or modify it under the terms of:
 *   - the GNU General Public License as published by the Free Software Foundation;
 *     either version 2 of the License, or (at your option) any later version.
 *   - or The European Software License; either version 1.1 or (at your option) any later version.
 * Date: 11/01/11
 * You may contact the copyright holder at: soporte.afirma5@mpt.es
 */

package es.gob.afirma.signers.cms;

import java.util.Enumeration;
import java.util.logging.Logger;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;

import es.gob.afirma.signers.pkcs7.BCChecker;

/** Clase que permite verificar si unos datos se corresponden con una firma CMS. */
final class ValidateCMSSignedData {

    private ValidateCMSSignedData() {
        // No permitimos la instanciacion
    }

    /** M&eacute;todo que verifica que es una firma de tipo "Signed data"
     * @param data
     *        Datos CMS.
     * @return si es de este tipo. */
    public static boolean isCMSSignedData(final byte[] data) {
    	new BCChecker().checkBouncyCastle();
        boolean isValid = true;
        try {
            final ASN1InputStream is = new ASN1InputStream(data);
            final ASN1Sequence dsq = (ASN1Sequence) is.readObject();
            final Enumeration<?> e = dsq.getObjects();
            // Elementos que contienen los elementos OID Data
            final DERObjectIdentifier doi = (DERObjectIdentifier) e.nextElement();
            if (!doi.equals(PKCSObjectIdentifiers.signedData)) {
                isValid = false;
            }
            else {
                // Contenido de SignedData
                final ASN1TaggedObject doj = (ASN1TaggedObject) e.nextElement();
                final ASN1Sequence datos = (ASN1Sequence) doj.getObject();
                final SignedData sd = new SignedData(datos);
                final ASN1Set signerInfosSd = sd.getSignerInfos();

                for (int i = 0; isValid && i < signerInfosSd.size(); i++) {
                    final SignerInfo si = new SignerInfo((ASN1Sequence) signerInfosSd.getObjectAt(i));
                    isValid = verifySignerInfo(si);
                }
            }
        }
        catch (final Exception ex) {
            // Logger.getLogger("es.gob.afirma").severe("Error durante el proceso de conversion "
            // + ex);
            isValid = false;
        }
        return isValid;
    }

    /** M&eacute;todo que verifica que los SignerInfos tenga el par&aacute;metro
     * que identifica que es de tipo cades.
     * @param si
     *        SignerInfo para la verificaci&oacute;n del p&aacute;rametro
     *        adecuado.
     * @return si contiene el par&aacute;metro. */
    private static boolean verifySignerInfo(final SignerInfo si) {
        boolean isSignerValid = true;
        final ASN1Set attrib = si.getAuthenticatedAttributes();
        final Enumeration<?> e = attrib.getObjects();
        Attribute atribute;
        while (isSignerValid && e.hasMoreElements()) {
            atribute = new Attribute((ASN1Sequence) e.nextElement());
            // si tiene la pol&iacute;tica es CADES.
            if (atribute.getAttrType().equals(PKCSObjectIdentifiers.id_aa_ets_sigPolicyId)) {
                isSignerValid = false;
                Logger.getLogger("es.gob.afirma").warning("El signerInfo no es del tipo CMS, es del tipo CADES"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        return isSignerValid;
    }
}
