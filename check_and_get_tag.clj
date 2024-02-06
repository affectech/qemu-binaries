(require 
 '[org.httpkit.client :as http])

(defn get-latest-qemu-version []
  (if-let [resp (deref (http/get "https://download.qemu.org/" {:as :text}) 5000 nil)]
    (last (last (re-seq #"qemu-([\w\.\-]+)\.tar\.xz(?=<)" (:body resp))))
    nil))

(if-let [resp (http/get 
               "https://api.github.com/repos/ktzh/qemu-builds/tags?per_page=1" 
               {:as :text})]  
  (if-let [qemu-version (get-latest-qemu-version)]
    ; from the right side of =, we're getting the name key
    (if (= qemu-version (second (first (first (json/parse-string (:body @resp)))))) 
      (System/exit 1) ;; if it matches stop the job, because we already have the latest release 
      (println qemu-version)) ;; if it doesn't (maybe we don't have any tags, for example) continue the job
    (System/exit 1)) ;; in all other cases just exit with code 1 
  (System/exit 1))