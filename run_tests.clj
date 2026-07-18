(require '[clojure.edn :as edn] '[clojure.java.io :as io] '[clojure.string :as str]
         '[clojure.test :as t])
(def c (edn/read-string (slurp "repository-contracts.edn")))
(doseq [p (:required c)] (assert (.isFile (io/file p)) (str "missing " p)))
(doseq [f (file-seq (io/file ".")) :when (and (.isFile f) (str/ends-with? (.getName f) ".edn")
                                               (not (str/includes? (.getPath f) "/.git/")))]
  (edn/read-string (slurp f)))
(let [bad (for [f (file-seq (io/file ".")) :when (and (.isFile f)
                 (not (str/includes? (.getPath f) "/.git/"))
                 (some #(str/ends-with? (.getName f) %) (:forbidden-extensions c)))] f)]
  (assert (empty? bad) (str "forbidden " bad)))
(def nss '[watari.murakumo-test watari.methods.test-ingest watari.methods.test-analyze
           watari.methods.test-autorun watari.methods.test-charter-gates
           watari.methods.test-kotoba-cid watari.methods.test-pipeline-cid])
(doseq [n nss] (require n))
(let [r (apply t/run-tests nss)] (System/exit (if (zero? (+ (:fail r) (:error r))) 0 1)))
