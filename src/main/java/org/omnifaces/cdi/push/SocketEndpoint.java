/*
 * Copyright 2015 OmniFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.cdi.push;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

import org.omnifaces.cdi.PushContext;
import org.omnifaces.config.BeanManager;

/**
 * <p>
 * The web socket server endpoint of <code>&lt;o:socket&gt;</code>.
 *
 * @author Bauke Scholtz
 * @see Socket
 * @since 2.3
 */
public class SocketEndpoint extends Endpoint {

	// Constants ------------------------------------------------------------------------------------------------------

	/** The URI template of the web socket endpoint. */
	static final String URI_TEMPLATE = PushContext.URI_PREFIX + "/{" + SocketPushContext.PARAM_CHANNEL + "}";

	private static final Logger logger = Logger.getLogger(SocketEndpoint.class.getName());
	private static final String ERROR_EXCEPTION = "SocketEndpoint: An exception occurred during processing web socket request.";

	// Actions --------------------------------------------------------------------------------------------------------

	/**
	 * Add given web socket session to the push context.
	 * @param session The opened web socket session.
	 * @param config The endpoint configuration.
	 */
	@Override
	public void onOpen(Session session, EndpointConfig config) {
		BeanManager.INSTANCE.getReference(SocketPushContext.class).add(session); // @Inject in Endpoint doesn't work in Tomcat+Weld.
	}

	/**
	 * Delegate exceptions to logger.
	 * @param session The errored web socket session.
	 * @param throwable The cause.
	 */
	@Override
	public void onError(Session session, Throwable throwable) {
		logger.log(Level.SEVERE, ERROR_EXCEPTION, throwable);
	}

	/**
	 * Remove given web socket session from the push context.
	 * @param session The closed web socket session.
	 * @param reason The close reason.
	 */
	@Override
	public void onClose(Session session, CloseReason reason) {
		BeanManager.INSTANCE.getReference(SocketPushContext.class).remove(session); // @Inject in Endpoint doesn't work in Tomcat+Weld and CDI.current() doesn't work in WildFly.
	}

}