/*
 * Este fichero forma parte del Cliente @firma.
 * El Cliente @firma es un aplicativo de libre distribucion cuyo codigo fuente puede ser consultado
 * y descargado desde www.ctt.map.es.
 * Copyright 2009,2010,2011 Gobierno de Espana
 * Este fichero se distribuye bajo  bajo licencia GPL version 2  segun las
 * condiciones que figuran en el fichero 'licence' que se acompana. Si se distribuyera este
 * fichero individualmente, deben incluirse aqui las condiciones expresadas alli.
 */

package es.gob.afirma.keystores.callbacks;

import javax.security.auth.callback.PasswordCallback;

/** PasswordCallbak que almacena internamente y devuelve la contrase&ntilde;a con la que se
 * construy&oacute; o la que se le establece posteriormente. */
public final class CachePasswordCallback extends PasswordCallback {

    private static final long serialVersionUID = 816457144215238935L;

    /** Contruye una Callback con una contrase&ntilda; preestablecida.
     * @param password
     *        Contrase&ntilde;a por defecto. */
    public CachePasswordCallback(final char[] password) {
        super(">", false); //$NON-NLS-1$
        this.setPassword(password);
    }
}
