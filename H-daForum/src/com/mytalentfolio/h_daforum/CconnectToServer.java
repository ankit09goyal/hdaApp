package com.mytalentfolio.h_daforum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Base64;

import com.mytalentfolio.h_daforum.R;
/**
 * <p>Copyright (c) Hochschule Darmstadt. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.</p>
 *
 * <p>You can redistribute and/or modify the code
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.</p> 
 *
 * <p>Please contact Hochschule Darmstadt if you need additional information or have any
 * questions.</p>
 * 
 * <p><strong>The Class CconnectToServer.</strong></br>
 * This class is used to form a secure connection between server and client</p>
 * 
 * @author Deepak Patel
 * @version 1.1
 */
public class CconnectToServer {

	/**
	 * Static variable for storing the Context. This context variable is
	 * used for reading the certificate file.
	 */
	static public Context mContext;

	/**
	 * {@code connect} is for forming the secure connection between server and
	 * android, sending and receiving of the data.
	 * 
	 * @param arg0
	 *            data which is to be sent to server.
	 * 
	 * @return data in string format, received from the server.
	 */
	public String connect(String... arg0) {

		int nrOfDataToSendToServer = arg0.length;
		nrOfDataToSendToServer = nrOfDataToSendToServer - 1;
		boolean valid = false;
		String dataFromServer = "unverified", serverPublicKeySigStr, serverDataSig;

		try {
			//Creating the server certificate
			Certificate serverCertificate = getServerCertificate();
		
			KeyStore keyStore = getKeyStore(serverCertificate);

			TrustManagerFactory tmf = getTrustManager(keyStore);

			SSLContext sslContext = getSSLContext(tmf);

			HostnameVerifier hostnameVerifier = new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			HttpsURLConnection urlConnection = getURLConnection(sslContext,
					hostnameVerifier);

			// Converting the data into JSONObject
			JSONObject obj = new JSONObject();
			for (int i = 0; i <= nrOfDataToSendToServer; i++) {
				obj.put("param" + i, arg0[i]);
			}

			// Converting the JSONObject into string
			String dataToSend = obj.toString();

			KeyPairGenerator keyGen = getKeyPairGenerator();

			KeyPair keyPair = keyGen.generateKeyPair();
			//Public key for verifying the digital signature
			PublicKey clientPublicKeySig = keyPair.getPublic();
			//Private key for signing the data
			PrivateKey clientPrivateKeySig = keyPair.getPrivate();

			// Get signed data
			String sigData = getDataSig(clientPrivateKeySig, dataToSend);

			// Creating URL Format
			String urlData = URLEncoder.encode("clientPublicKeySig", "UTF-8")
					+ "="
					+ URLEncoder.encode(Base64.encodeToString(
							clientPublicKeySig.getEncoded(), Base64.DEFAULT),
							"UTF-8");
			urlData += "&" + URLEncoder.encode("clientData", "UTF-8") + "="
					+ URLEncoder.encode(dataToSend, "UTF-8");
			urlData += "&" + URLEncoder.encode("clientDataSig", "UTF-8") + "="
					+ URLEncoder.encode(sigData, "UTF-8");

			// Sending the data to the server
			OutputStreamWriter wr = new OutputStreamWriter(
					urlConnection.getOutputStream());
			wr.write(urlData);
			wr.flush();
			wr.close();

			// Receiving the data from server
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;

			// Read Server Response
			while ((line = reader.readLine()) != null) {
				// Append server response in string
				sb.append(line + "\n");
				// sb.append(line);
			}
			String text = sb.toString();
			reader.close();

			// Extracting the data, public key and signature received from
			// server
			Vector<String> storeExtractedValues = new Vector<String>();

			storeExtractedValues = extractDataFromJson(text, "data");
			dataFromServer = storeExtractedValues.get(0);

			storeExtractedValues = extractDataFromJson(text,
					"serverPublicKeySig");
			serverPublicKeySigStr = storeExtractedValues.get(0);

			storeExtractedValues = extractDataFromJson(text, "serverDataSig");
			serverDataSig = storeExtractedValues.get(0);

			// Converting the Server Public key format to Java compatible from
			PublicKey serverPublicKeySig = getServerPublicKey(serverPublicKeySigStr);

			// Verify the received data
			valid = getDataValidity(serverPublicKeySig, dataFromServer,
					serverDataSig);

			// Disconnect the url connection
			urlConnection.disconnect();

			if (dataFromServer.equalsIgnoreCase("unverified")) {
				CExceptionHandling.ExceptionState = ExceptionSet.SENT_DATA_UNVERIFIED;
				return "-1";
			} else if (valid == false) {
				CExceptionHandling.ExceptionState = ExceptionSet.RECEIVED_DATA_UNVERIFIED;
				return "-1";
			} else {
				return dataFromServer;
			}

		} catch (Exception e) {
			CExceptionHandling.ExceptionMsg = e.getMessage();

			if (e.toString().equals(
					"java.net.SocketException: Network unreachable")) {
				CExceptionHandling.ExceptionState = ExceptionSet.NO_DATA_CONNECTION;
			} else if (e
					.toString()
					.equals("java.net.SocketTimeoutException: failed to connect to /10.0.2.2 (port 443) after 10000ms")) {
				CExceptionHandling.ExceptionState = ExceptionSet.CONNECTION_TIMEOUT;
			} else {
				CExceptionHandling.ExceptionState = ExceptionSet.OTHER_EXCEPTIONS;
			}
			return "-1";
		}

	}

	/**
	 * Creates a new instance of {@code Certificate}
	 * 
	 * @return the new {@code Certificate} instance.
	 * @throws CertificateException
	 *             if the specified certificate type is not available at any
	 *             installed provider.
	 * @throws IOException
	 *             if an error occurs while closing this stream
	 */
	private Certificate getServerCertificate() throws CertificateException,
	IOException {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		// deepak
		InputStream caInput = mContext.getResources().openRawResource(
				R.raw.server);
		// ankit
		// InputStream caInput =
		// mContext.getResources().openRawResource(R.raw.localhost);

		Certificate ca;
		try {
			ca = cf.generateCertificate(caInput);
		} finally {
			caInput.close();
		}

		return ca;
	}

	/**
	 * Creates a new instance of KeyStore from provided certificate.
	 * 
	 * @param ca
	 *            the certificate to get KeyStore
	 * @return the new {@code KeyStore} instance.
	 * @throws KeyStoreException
	 *             if an error occurred during the creation of the new KeyStore.
	 * @throws NoSuchAlgorithmException
	 *             if the required algorithm is not available.
	 * @throws CertificateException
	 *             if the specified certificate type is not available at any
	 *             installed provider.
	 * @throws IOException
	 *             if an error occurs while closing this stream
	 */
	private KeyStore getKeyStore(Certificate ca) throws KeyStoreException,
	NoSuchAlgorithmException, CertificateException, IOException {
		// Create a KeyStore containing our trusted CAs
		String keyStoreType = KeyStore.getDefaultType();
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		keyStore.load(null, null);
		keyStore.setCertificateEntry("ca", ca);
		return keyStore;

	}

	/**
	 * Creates a new instance of {@code TrustManagerFactory} from provided
	 * {@code KeyStore}.
	 * 
	 * @param keyStore
	 *            the KeyStore to get the TrustManagerFactory
	 * @return the new {@code TrustManagerFactory} instance.
	 * @throws KeyStoreException
	 *             if an error occurred during the creation of the new KeyStore.
	 * @throws NoSuchAlgorithmException
	 *             if the required algorithm is not available.
	 * 
	 */
	private TrustManagerFactory getTrustManager(KeyStore keyStore)
			throws NoSuchAlgorithmException, KeyStoreException {
		// Create a TrustManager that trusts the CAs in our KeyStore
		String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
		tmf.init(keyStore);
		return tmf;
	}

	/**
	 * Creates a new instance of {@code SSLContext} from the given
	 * {@code TrustManagerFactory} {@code tmf}.
	 * 
	 * @param tmf
	 *            the TrustManagerFactory to create a SSLContext
	 * @return the new {@code SSLContext} instance.
	 * @throws NoSuchAlgorithmException
	 *             if the required algorithm is not available.
	 * @throws KeyManagementException
	 *             if initializing this instance fails.
	 */
	private SSLContext getSSLContext(TrustManagerFactory tmf)
			throws NoSuchAlgorithmException, KeyManagementException {

		// Create an SSLContext that uses our TrustManager
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, tmf.getTrustManagers(), null);
		return context;
	}

	/**
	 * Creates a new instance of {@code HttpsURLConnection} from the given
	 * {@code context} and {@code hostnameVerifier}.
	 * 
	 * @param context
	 *            the TrustManagerFactory to get the SSLContext
	 * @return the new {@code HttpsURLConnection} instance.
	 * @throws IOException
	 *             if an error occurs while opening the connection.
	 */
	private HttpsURLConnection getURLConnection(SSLContext context,
			HostnameVerifier hostnameVerifier) throws IOException {

		URL url = new URL("https://10.0.2.2/mycode/digitalSig.php");

		HttpsURLConnection urlConnection = (HttpsURLConnection) url
				.openConnection();
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.setConnectTimeout(3000);
		urlConnection.setSSLSocketFactory(context.getSocketFactory());
		urlConnection.setHostnameVerifier(hostnameVerifier);

		return urlConnection;
	}

	/**
	 * Creates a new instance of (@code KeyPairGenerator}.
	 * 
	 * @return the new {@code KeyPairGenerator} instance.
	 * @throws NoSuchAlgorithmException
	 *             if the specified algorithm is not available
	 */
	private KeyPairGenerator getKeyPairGenerator()
			throws NoSuchAlgorithmException {
		// Generate Key Pair
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		return keyGen;
	}

	/**
	 * Returns the string formatted digital signature for the data.
	 * 
	 * @param key
	 *            Private key for signing the data.
	 * @param data
	 *            Data for which the signature is to be generated.
	 * @return signed data with the provide private key.
	 * @throws NoSuchAlgorithmException
	 *             if the specified algorithm is not available.
	 * @throws InvalidKeyException
	 *             if privateKey is not valid.
	 * @throws SignatureException
	 *             if this Signature instance is not initialized properly.
	 */
	private String getDataSig(PrivateKey key, String data)
			throws NoSuchAlgorithmException, InvalidKeyException,
			SignatureException {

		// Generate Signature For the data
		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initSign(key);
		signature.update(data.getBytes());
		byte[] sigBytes = signature.sign();
		return Base64.encodeToString(sigBytes, Base64.DEFAULT);
	}

	/**
	 * Creates a new instance of {@code PublicKey}. Convert the string formatted
	 * public key into {@code PublicKey} type.
	 * 
	 * @param key
	 *            the string formated public key.
	 * @return the new {@code PublicKey} instance.
	 * @throws NoSuchAlgorithmException
	 *             if no provider provides the requested algorithm.
	 * @throws InvalidKeyException
	 *             if the specified keySpec is invalid.
	 * 
	 */
	// Converting the Server Public key format to Java compatible from
	private PublicKey getServerPublicKey(String key)
			throws NoSuchAlgorithmException, InvalidKeySpecException {

		// Converting the Server Public key format to Java compatible from
		key = key.replace("-----BEGIN PUBLIC KEY-----\n", "");
		key = key.replace("\n-----END PUBLIC KEY-----", "");

		// Creating the public key from the string format received from server
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey serverPublicKeySig = keyFactory
				.generatePublic(new X509EncodedKeySpec(Base64.decode(
						key.toString(), Base64.DEFAULT)));
		return serverPublicKeySig;
	}

	/**
	 * {@code getDataValidity} is used to verify the digital signature of the
	 * received data. If the verification is true then {@code true} is returned
	 * otherwise {@code false}.
	 * 
	 * @param key
	 *            the PublicKey received from server.
	 * @param data
	 *            the data received from server.
	 * @param serverDataSig
	 *            the Signature corresponding to the received data.
	 * @return Boolean value.
	 * @throws NoSuchAlgorithmException
	 *             if the specified algorithm is not available.
	 * @throws InvalidKeyException
	 *             if publicKey is not valid.
	 * @throws SignatureException
	 *             if this Signature instance is not initialized properly.
	 */
	private Boolean getDataValidity(PublicKey key, String data,
			String serverDataSig) throws NoSuchAlgorithmException,
			InvalidKeyException, SignatureException {
		Signature s = Signature.getInstance("SHA256withRSA");
		s.initVerify(key);
		byte[] byte_dataFromServer = data.getBytes();
		s.update(byte_dataFromServer);
		byte[] byte_serverDataSig = Base64
				.decode(serverDataSig, Base64.DEFAULT);
		Boolean valid = s.verify(byte_serverDataSig);
		return valid;
	}

	/**
	 * This method {@code extractDataFromJson} is used to extract the data from
	 * JSONArray
	 * 
	 * @param arg0
	 *            [0]-JSONArray formated data, arg[1]-contains the tag name
	 *            corresponding to which the data is to be extracted.
	 * @return Vector<String>, array of values extracted.
	 */
	public Vector<String> extractDataFromJson(String... arg0) {
		Vector<String> valuesExtractedFromData = new Vector<String>();
		try {
			JSONArray json = new JSONArray(arg0[0]);
			for (int i = 0; i < json.length(); i++) {
				JSONObject obj = json.getJSONObject(i);
				valuesExtractedFromData.add(obj.getString(arg0[1]));
			}
			return valuesExtractedFromData;
		} catch (JSONException e) {

			e.printStackTrace();
			valuesExtractedFromData.add("error");
			return valuesExtractedFromData;
		}
	}

}