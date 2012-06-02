(ns eu.stratuslab.client.compute
  "Contains commands to interact with OpenNebula."
  (:use [eu.stratuslab.client.configuration :only (*cfg*)])
  (:require [clojure.xml :as xml])
  (:import [org.apache.xmlrpc.client XmlRpcClient XmlRpcClientConfigImpl]
           [java.net URL]
           [java.io ByteArrayInputStream]))

(defn get-struct-map [xml]
  (let [stream (ByteArrayInputStream. (.getBytes (.trim xml)))]
    (xml/parse stream)))

(defn create-xmlrpc-client []
  (let [config (XmlRpcClientConfigImpl.)
        client (XmlRpcClient.)
        endpoint (:endpoint *cfg*)
        username (:username *cfg*)
        password (:password *cfg*)
        url (URL. (str "https://" endpoint ":2634/pswd/xmlrpc"))]
    (.setServerURL config url)
    (.setBasicUserName config username)
    (.setBasicPassword config password)
    (.setConfig client config)
    client))

(defn- pack-params [& args]
  (into-array java.lang.Object args))

(defn- list-instances []
  (let [client (create-xmlrpc-client)
        args (pack-params "dummy"
                          (Integer. -3)
                          (Integer. -1)
                          (Integer. -1)
                          (Integer. -1))]
    (.execute client "one.vmpool.info" args)))

(defn- start-instance [vm-template]
  (let [client (create-xmlrpc-client)
        args (pack-params "dummy" vm-template)]
    (.execute client "one.vm.allocate" args)))

(defn- kill-instance [vm-id]
  (let [client (create-xmlrpc-client)
        args (pack-params "dummy" "finalize" vm-id)]
    (.execute client "one.vm.action" args)))

(defn create-template [image-id] 
  (str "
CPU    = 1
VCPU    = 1
MEMORY = 128
RANK = \"- RUNNING_VMS\"


DISK = [
source   = \"http://marketplace.stratuslab.eu/metadata/"
    image-id
    "\",
target   = \"hda\",
save     = no,
readonly = \"no\",
driver = \"raw\"
]
DISK = [
type     = swap,
size     = 1024,
target   = \"hdb\",
readonly = \"no\"
]



NIC = [ network_uname=oneadmin,network = \"public\" ]

GRAPHICS = [
port = \"-1\",
type = \"vnc\"
]

CONTEXT = [
public_key = \"ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAwSwt4WTABnlabGt62u1fjXaw7zhMKX/bm1++m/cLPxgTiRJLor/K2QW10l0lE5/yOU/OQV8KGbKu+xemfkkXdIMzXNQFaD+GsT1GrbUayl5v0DiBVS24Hd8j8TmdIzpldxWEYvAPyjurUsNGwn72XMTMhNIC2CVvu205lZVjdOTs75Wjvnas/0/xvpuzYYFGVjCd5dJJNIwlglp/8a68Lldl/MRaxGbKnYGRqvuV9SXQNaOlfYLqcok9PynEvBTA+MSn1HPvHY0EGqyYGJEmlFTCM2chbKPSo1jwnXgcxAzW2Olvkl9720kOOp7XVOlLdGIMqT6IX1U8JBq2tE/Fuw== loomis
\",

target = \"hdd\"
]
"))

(defmulti compute
  (fn [& args] (first args))
  :default nil)

(defmethod compute nil
  [& args]
  "don't know how to deal with nil")

(defmethod compute :list
  [& args]
  (let [[flag info] (list-instances)]
    (get-struct-map info)))

(defmethod compute :start
  [& args]
  (let [identifier (second args)
        vm-template (create-template identifier)
        [flag info] (start-instance vm-template)]
    info))

(defmethod compute :kill
  [& args]
  (let [vmId (int (second args))
        [flag info] (kill-instance vmId)]
    info))
