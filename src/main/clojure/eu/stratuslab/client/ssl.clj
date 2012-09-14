(ns eu.stratuslab.client.ssl
  (:import [javax.net.ssl
            HostnameVerifier
            HttpsURLConnection
            SSLContext
            SSLSession
            TrustManager
            X509TrustManager]
           [java.security SecureRandom]))

(defn- create-tm 
  "The created X509TrustManager will trust all certificates."
  []
  (reify 
    X509TrustManager
    (checkClientTrusted[this chain authType] nil)
    (checkServerTrusted[this chain authType] nil)
    (getAcceptedIssuers[this] nil)))

(defn- create-hnv
  "The created HostnameVerifier will trust all hosts
   and sessions."
  []
  (reify 
    HostnameVerifier
    (verify[this hostname session] true)))

(defn init-ssl-context
  "Forces the default https socket handlers to trust all certificates."
  []
  (let [tm (into-array TrustManager [(create-tm)])
        hnv (create-hnv)
        sc (SSLContext/getInstance "SSL")]
    (.init sc nil tm (SecureRandom.))
    (HttpsURLConnection/setDefaultSSLSocketFactory (.getSocketFactory sc))
    (HttpsURLConnection/setDefaultHostnameVerifier hnv)))
