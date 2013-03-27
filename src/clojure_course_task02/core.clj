(ns clojure-course-task02.core
  (:gen-class)
   :import java.io.File)

(defn pmapcat [f batches]
  (->> batches
    (pmap f)
    (apply concat)
    doall))

;(defn file-filter [predicate]
;  (reify java.io.FilenameFilter
;    (accept [_ dir name] (predicate dir name))))

;(defn regexp-filter [re] (file-filter #((not (nil? (re-find re %2))))))
;(def directory-filter (file-filter #(.isDirectory (File. % %2))))
; Не удалось заставить работать эти функции ^^ - нужна помощь.

(defn regexp-filter [re]
  (reify java.io.FilenameFilter
    (accept [_ dir name] (not (nil? (re-find re name))))))

(def directory-filter
  (reify java.io.FilenameFilter
    (accept [_ dir name] (.isDirectory (File. dir name)))))

(defn find-files [file-name path]
  (let [file (File. path)]
  (concat (->> file-name
               re-pattern
               regexp-filter
               (.listFiles file )
               (map #(.getName %)))
          (->> directory-filter
               (.listFiles file)
               (map #(.getPath %) )
               (pmapcat #(find-files file-name %))))))

; Или так? 
;(defn find-files [file-name path]
;  (let [file (File. path)]
;    (concat (map #(.getName %) (.listFiles file (regexp-filter (re-pattern file-name))))
;      (pmapcat #(find-files file-name %) (map #(.getPath %) (.listFiles file directory-filter))))))

(defn usage []
  (println "Usage: $ run.sh file_name path"))

(defn -main [file-name path]
  (if (or (nil? file-name)
          (nil? path))
    (usage)
    (do
      (println "Searching for " file-name " in " path "...")
      (dorun (map println (find-files file-name path))))))
