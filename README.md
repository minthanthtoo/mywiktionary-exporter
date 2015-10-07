#mywiktionary-exporter
Extract and export Burmese/Myanmar entries from [Myanmar Wiktionary site](http://my.wiktionary.org/)

#About files and folders
*'java' folder contains an Eclipse Java project that extract and export data from wiktionary xml file into MySql database
*'exports' folder contains database export files, such as CSV(table data),sql(table structure)
*'java/data/' contains wiktionary xml file, files manually extracted from wiktionary xml file.
*'java/analysis/' contains analysis results of wiktionary xml file

#How-to
*Download "mywiktionary-20150901-pages-articles-multistream.xml.bz2" file from https://dumps.wikimedia.org/mywiktionary/20150901/
*Extract the containing xml file into '[project dir]/java/data/' folder
*Find and delete contents from the extracted file that are exactly the same as those of files with '_excluded_' extension.
*Create required MySql tables by importing the sql file under '[project dir]/exports'
*Run the java project under '[project dir]/java/' dir

#Limits
It can -
* detect word language, pronunciation, etymology, part-of-speech, definition, example, synonyms, antonyms, related words, translations, etc.
* input them into well structured dictionary tables

It cannot -
* correct human input errors

#License
MIT License: You can do whatever you want with these contents. But use them at your own risk.

#Contact us
email:mithanthtoo1994@gmail.com