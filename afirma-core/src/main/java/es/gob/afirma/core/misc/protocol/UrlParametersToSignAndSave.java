/* Copyright (C) 2011 [Gobierno de Espana]
 * This file is part of "Cliente @Firma".
 * "Cliente @Firma" is free software; you can redistribute it and/or modify it under the terms of:
 *   - the GNU General Public License as published by the Free Software Foundation;
 *     either version 2 of the License, or (at your option) any later version.
 *   - or The European Software License; either version 1.1 or (at your option) any later version.
 * You may contact the copyright holder at: soporte.afirma@seap.minhap.es
 */

package es.gob.afirma.core.misc.protocol;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.signers.ExtraParamsProcessor;
import es.gob.afirma.core.signers.ExtraParamsProcessor.IncompatiblePolicyException;

/** Par&aacute;metros de la URL de llamada a la aplicaci&oacute;n de la operaci&oacute;n
 * de firma y guardado de resultados. */
public final class UrlParametersToSignAndSave extends UrlParameters {

	/** N&uacute;mero m&aacute;ximo de caracteres permitidos para el identificador
	 * de sesi&oacute;n de la firma. */
	private static final int MAX_ID_LENGTH = 20;

	/** Par&aacute;metro de entrada con el formato de firma. */
	private static final String CRYPTO_OPERATION_PARAM = "cop"; //$NON-NLS-1$

	/** Par&aacute;metro de entrada con el formato de firma. */
	private static final String FORMAT_PARAM = "format"; //$NON-NLS-1$

	/** Par&aacute;metro de entrada con el algoritmo de firma. */
	private static final String ALGORITHM_PARAM = "algorithm"; //$NON-NLS-1$

	/** Par&aacute;metro de entrada con el nombre propuesto para un fichero. */
	private static final String FILENAME_PARAM = "filename"; //$NON-NLS-1$

	/** Par&aacute;metro de entrada con el identificador del documento. */
	private static final String ID_PARAM = "id"; //$NON-NLS-1$

	/** Par&aacute;metro de entrada con la m&iacute;nima versi&oacute;n requerida del aplicativo
	 * a usar en la invocaci&oacute;n por protocolo. */
	private static final String VER_PARAM = "ver"; //$NON-NLS-1$

	/** Par&aacute;metro de entrada que nos dice si tenemos que usar un provatekeyentry fijado o fijar uno nuevo. */
	private static final String STICKY_PARAM = "sticky"; //$NON-NLS-1$

	/** Par&aacute;metro de entrada que nos dice si tenemos que ignorar
	 * cla <code>PrivateKeyEntry</code> fijada. */
	private static final String RESET_STICKY_PARAM = "resetsticky"; //$NON-NLS-1$

	/**
	 * Par&aacute;metros reconocidos. Se utilizaran para identificar los parametros desconocidos
	 * introducidos por el JavaScript.
	 */
	private static final String[] KNOWN_PARAMETERS = new String[] {
			CRYPTO_OPERATION_PARAM, FORMAT_PARAM, ALGORITHM_PARAM, FILENAME_PARAM, ID_PARAM,
			VER_PARAM, STICKY_PARAM, RESET_STICKY_PARAM, PROPERTIES_PARAM, DATA_PARAM,
			GZIPPED_DATA_PARAM, RETRIEVE_SERVLET_PARAM, STORAGE_SERVLET_PARAM, KEY_PARAM,
			FILE_ID_PARAM, KEYSTORE_OLD_PARAM, KEYSTORE_PARAM, ACTIVE_WAITING_PARAM,
			MINIMUM_CLIENT_VERSION_PARAM
	};

	/** Algoritmos de firma soportados. */
	private static final Set<String> SUPPORTED_SIGNATURE_ALGORITHMS = new HashSet<>();
	static {
		SUPPORTED_SIGNATURE_ALGORITHMS.add("SHA1withRSA"); //$NON-NLS-1$
		SUPPORTED_SIGNATURE_ALGORITHMS.add("SHA256withRSA"); //$NON-NLS-1$
		SUPPORTED_SIGNATURE_ALGORITHMS.add("SHA384withRSA"); //$NON-NLS-1$
		SUPPORTED_SIGNATURE_ALGORITHMS.add("SHA512withRSA"); //$NON-NLS-1$
	}

	private String operation;
	private String signFormat;
	private String signAlgorithm;
	private String minimumProtocolVersion;

	/** Opci&oacute;n de configuraci&oacute;n que determina si se debe mantener
	 * el primer certificado seleccionado para todas las operaciones. */
	private boolean sticky;

	/** Opci&oacute;n de configuraci&oacute;n que determina si se debe ignorar
	 * cualquier certificado prefijado. */
	private boolean resetSticky;

	/** Colecci&oacute;n con los par&aacute;metros no reconocidos (podr&iacute;an reconocerlos los <i>plugins</i>). */
	private final Map<String, String> anotherParams = new HashMap<>();

	/**
	 * Construye el conjunto de par&aacute;metros vac&iacute;o.
	 */
	public UrlParametersToSignAndSave() {
		setData(null);
		setFileId(null);
		setRetrieveServletUrl(null);
	}

	/** Obtiene la versi&oacute;n m&iacute;nima requerida del aplicativo.
	 * @return Versi&oacute;n m&iacute;nima requerida del aplicativo. */
	public String getMinimumProtocolVersion() {
		return this.minimumProtocolVersion;
	}

	/** Obtiene el tipo de operaci&oacute;n a realizar (firma, cofirma o contrafirma).
	 * @return Operaci&oacute;n. */
	public String getOperation() {
		return this.operation;
	}

	/** Obtiene el formato de firma.
	 * @return Formato de firma */
	public String getSignatureFormat() {
		return this.signFormat;
	}

	/** Obtiene el algoritmo de firma.
	 * @return Algoritmo de firma */
	public String getSignatureAlgorithm() {
		return this.signAlgorithm;
	}

	void setOperation(final String operation) {
		this.operation = operation;
	}

	/** Establece el nombre del formato de firma que se debe utilizar.
	 * @param format Formato de firma. */
	public void setSignFormat(final String format) {
		this.signFormat = format;
	}

	void setSignAlgorithm(final String algo) {
		this.signAlgorithm = algo;
	}

	void setMinimumProtocolVersion(final String minVer) {
		this.minimumProtocolVersion = minVer;
	}

	/** Obtiene la opci&oacute;n de configuraci&oacute;n <i>sticky</i>.
	 * @return Opci&oacute;n de configuraci&oacute;n que determina si se debe
	 *         mantener el primer certificado seleccionado ({@code true}) o se
	 *         debe pedir siempre que el usuario elija uno ({@code false}). */
	public boolean getSticky() {
		return this.sticky;
	}

	/** Establece la opci&oacute;n de configuraci&oacute;n <i>sticky</i>.
	 * @param sticky Opci&oacute;n de configuraci&oacute;n que determina si se debe
	 *               mantener el primer certificado seleccionado ({@code true}) o se
	 *               debe pedir siempre que el usuario elija uno ({@code false}). */
	public void setSticky(final boolean sticky) {
		this.sticky = sticky;
	}

	/** Establece la opci&oacute;n de configuraci&oacute;n <i>resetsticky</i>.
	 * @param resetSticky Opci&oacute;n de configuraci&oacute;n que determina si se debe
	 *         ignorar el certificado mantener el primer certificado seleccionado ({@code true})
	 *         o si se puede utilizar en caso de que se solicite ({@code false}). */
	public void setResetSticky(final boolean resetSticky) {
		this.resetSticky = resetSticky;
	}

	/** Obtiene la opci&oacute;n de configuraci&oacute;n <i>resetsticky</i>.
	 * @return Opci&oacute;n de configuraci&oacute;n que determina si se debe
	 *         ignorar el cualquier certificado seleccionado ({@code true}) o si
	 *         deber&iacute;a usarse este si as&iacute; se indica ({@code false}). */
	public boolean getResetSticky() {
		return this.resetSticky;
	}

	/** Establece los par&aacute;metros de la operaci&oacute;n de firma y guardado de resultados.
	 * @param params Par&aacute;metros de la operaci&oacute;n de firma y guardado de resultados.
	 * @throws ParameterException Si se proporciona un par&aacute;metro inv&aacute;lido o incorrecto.
	 */
	public void setSignAndSaveParameters(final Map<String, String> params) throws ParameterException {

		// Comprobamos que el identificador de sesion de la firma no sea mayor de un cierto numero de caracteres
		String signatureSessionId = null;
		if (params.containsKey(ID_PARAM)) {
			signatureSessionId = params.get(ID_PARAM);
		}
		else if (params.containsKey(FILE_ID_PARAM)) {
			 signatureSessionId = params.get(FILE_ID_PARAM);
		}

		if (signatureSessionId != null) {
			if (signatureSessionId.length() > MAX_ID_LENGTH) {
				throw new ParameterException(
					"La longitud del identificador para la firma es mayor de " + MAX_ID_LENGTH + " caracteres." //$NON-NLS-1$ //$NON-NLS-2$
				);
			}

			// Comprobamos que el identificador de sesion de la firma sea alfanumerico (se usara como nombre de fichero)
			for (final char c : signatureSessionId.toLowerCase(Locale.ENGLISH).toCharArray()) {
				if ((c < 'a' || c > 'z') && (c < '0' || c > '9')) {
					throw new ParameterException("El identificador de la firma debe ser alfanumerico."); //$NON-NLS-1$
				}
			}

			setSessionId(signatureSessionId);
		}

		// Version minima requerida del protocolo que se debe soportar
		if (params.containsKey(VER_PARAM)) {
			setMinimumProtocolVersion(params.get(VER_PARAM));
		}
		else {
			setMinimumProtocolVersion(Integer.toString(ProtocolVersion.VERSION_0.getVersion()));
		}

		// Tomamos el tipo de operacion
		final String op = params.get(CRYPTO_OPERATION_PARAM);
		setOperation(op);

		// Si hemos recibido el identificador para la descarga de la configuracion,
		// no encontraremos el resto de parametros
		if (getFileId() != null) {
			return;
		}

		// Comprobamos la validez de la URL del servlet de guardado en caso de indicarse
		if (params.containsKey(STORAGE_SERVLET_PARAM)) {

			// Comprobamos que la URL sea valida
			URL storageServletUrl;
			try {
				storageServletUrl = validateURL(params.get(STORAGE_SERVLET_PARAM));
			}
			catch (final ParameterLocalAccessRequestedException e) {
				throw new ParameterLocalAccessRequestedException(
					"La URL del servicio de guardado no puede ser local: " + e, e //$NON-NLS-1$
				);
			}
			catch (final ParameterException e) {
				throw new ParameterException("Error al validar la URL del servicio de guardado: " + e, e); //$NON-NLS-1$
			}
			setStorageServletUrl(storageServletUrl);
		}

		// Comprobamos que se ha especificado el formato
		if (!params.containsKey(FORMAT_PARAM)) {
			throw new ParameterException("No se ha recibido el formato de firma"); //$NON-NLS-1$
		}

		final String format = params.get(FORMAT_PARAM);
		setSignFormat(format);

		// Comprobamos que se ha especificado el algoritmo
		if (!params.containsKey(ALGORITHM_PARAM)) {
			throw new ParameterException("No se ha recibido el algoritmo de firma"); //$NON-NLS-1$
		}
		final String algo = params.get(ALGORITHM_PARAM);
		if (!SUPPORTED_SIGNATURE_ALGORITHMS.contains(algo)) {
			throw new ParameterException("Algoritmo de firma no soportado: " + algo); //$NON-NLS-1$
		}

		setSignAlgorithm(algo);

		String props = null;
		if (params.containsKey(PROPERTIES_PARAM)) {
			props = params.get(PROPERTIES_PARAM);
		}

		if (props != null && !props.isEmpty()) {
			try {
				setExtraParams(AOUtil.base642Properties(props));
			}
			catch (final Exception e) {
				LOGGER.severe(
					"Las propiedades adicionales indicadas en el parametro '" + PROPERTIES_PARAM + "' no se han podido cargar: " + e //$NON-NLS-1$ //$NON-NLS-2$
				);
				setExtraParams(new Properties());
			}
		}
		else {
			setExtraParams(new Properties());
		}

		// Valor de parametro sticky
		if (params.containsKey(STICKY_PARAM)) {
			setSticky(Boolean.parseBoolean(params.get(STICKY_PARAM)));
		}
		else {
			setSticky(false);
		}

		// Valor de parametro resetsticky
		if (params.containsKey(RESET_STICKY_PARAM)) {
			setResetSticky(Boolean.parseBoolean(params.get(RESET_STICKY_PARAM)));
		}
		else {
			setResetSticky(false);
		}

		setDefaultKeyStore(getDefaultKeyStoreName(params));
		setDefaultKeyStoreLib(getDefaultKeyStoreLib(params));

		// Comprobamos si se ha proporcionado un nombre de fichero por defecto
		if (params.containsKey(FILENAME_PARAM)) {
			final String filename = params.get(FILENAME_PARAM);
			// Determinamos si el nombre tiene algun caracter que no consideremos valido para un nombre de fichero
			for (final char invalidChar : "\\/:*?\"<>|".toCharArray()) { //$NON-NLS-1$
				if (filename.indexOf(invalidChar) != -1) {
					throw new ParameterException(
						"Se ha indicado un nombre de fichero con el caracter invalido: " + invalidChar //$NON-NLS-1$
					);
				}
			}
			setFilename(params.get(FILENAME_PARAM));
		}
	}

	/** Expande los <code>extraParams</code> configurados en la URL que lo permitran. Por ejemplo,
	 * la pol&iacute;tica de firma establecida mediante "expPolicy" se expandir&aacute; a los
	 * valores correspondientes de la pol&iacute;tica.
	 * @throws IncompatiblePolicyException Cuando se hayan proporcionado par&aacute;metros
	 *                                     incompatibles con la pol&iacute;tica de firma configurada. */
	public void expandExtraParams() throws IncompatiblePolicyException {
		setExtraParams(
				ExtraParamsProcessor.expandProperties(
						getExtraParams(),
						this.data,
						getSignatureFormat()
						)
				);
	}


	/**
	 * Obtiene la colecci&oacute;n con los par&aacute;metros no reconocidos (podr&iacute;an reconocerlos los <i>plugins</i>).
	 * @return Colecci&oacute;n con los par&aacute;metros no reconocidos (podr&iacute;an reconocerlos los <i>plugins</i>).
	 */
	public Map<String, String> getAnotherParams() {
		return this.anotherParams;
	}

	/**
	 * Establece los par&aacute;metros desconocidos que se han encontrado entre los parametros de entrada.
	 * @param params Par&aacute;metros de entrada.
	 */
	public void setAnotherParams(final Map<String, String> params) {

		for (final String key : params.keySet().toArray(new String[0])) {
			if (!contains(KNOWN_PARAMETERS, key)) {
				this.anotherParams.put(key,  params.get(key));
			}
		}
	}

	/**
	 * Busca un elemento dentro de un listado.
	 * @param elements Elementos entre los que buscar.
	 * @param targetElement Elemento buscado.
	 * @return {@code true} si el elemento estaba en el listado, {@code false} en caso contrario.
	 */
	private static boolean contains(final String[] elements, final String targetElement) {
		for (final String element : elements) {
			if (targetElement.equals(element)) {
				return true;
			}
		}
		return false;
	}
}
