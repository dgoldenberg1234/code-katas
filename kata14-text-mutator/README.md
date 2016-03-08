## Summary
This is an initial implementation "spike" for the Kata 14 task from here:
http://codekata.pragprog.com/2007/01/kata_fourteen_t.html.

Reads in a set of text documents, extracts N-grams of configurable cardinality and allows the user
to create a new, mutated text based on the N-grams. Does so by picking randomized "suffixes"
(N-gram endings) for the "prefixes" (the first N-1 terms in the N-gram).

## Dependencies
- Accumulo - optionally selectable storage for the generated data/model. Accumulo is not required and is
included for spelunking purposes.
- Open NLP - used for sentence breaking.

## Sentence detection
The training set for the sentence detector and the generated model are included under ./resources/sentences.
The dg-en-sent.bin is the model.

## Interface

usage: com.hexastax.kata14.main.TextMutator `[-?]` `[-corpFileLoc <corpus file location>]` `[-corpName`
        `<corpus name>]` `[-h]` `[-help]` `[-maxNumPars <max. total num. paragraphs>]` `[-maxNumSentsPerPara`
        `<max. num. sentences per paragraph>]` `[-maxSentLen <max. sentence length>]` `[-modelType <ID`
        `field name>]` `[-ngramCard <N-gram cardinality>]`

```
OPTIONS:
 -?                                                        Print this message.
 -corpFileLoc <corpus file location>                       [Required]. The path to the corpus
                                                           directory on disk.
 -corpName <corpus name>                                   [Required]. The name of the document
                                                           corpus to use. Provide a user-friendly,
                                                           unique name for the corpus.
 -h                                                        Print this message.
 -help                                                     Print this message.
 -maxNumPars <max. total num. paragraphs>                  [Required]. The maximum number of
                                                           paragraphs to generate (positive
                                                           integer).
 -maxNumSentsPerPara <max. num. sentences per paragraph>   [Required]. The maximum number of
                                                           sentences per paragraph to generate
                                                           (positive integer).
 -maxSentLen <max. sentence length>                        [Required]. The maximum sentence length
                                                           (positive integer).
 -modelType <ID field name>                                [Required]. The type of model to use:
                                                           inmemory or accumulo.
 -ngramCard <N-gram cardinality>                           [Required]. The N-gram cardinality/size
                                                           (positive integer >= 2)
```

## Sample invokation

```
java com.hexastax.kata14.main.TextMutator
    -corpFileLoc ./resources/corpora/test-corpus-1.zip
    -corpName test-corpus-1
    -maxSentLen 30
    -maxNumPars 6
    -maxNumSentsPerPara 10
    -ngramCard 3
    -modelType inmemory
```

* Will generate a piece of mutated text based on the documents in the test-corpus-1.zip archive.
* Max sentence length will be 30 words.
* Max number of paragraphs generated will be 6.
* Max number of sentences per paragraph will be 10.
* N-gram cardinality will be 3 i.e. use trigrams.
* The storage type will be set to an in-memory persistence provider.

## Design notes

There are three aspects to the design: data ingestion, persistence, and actual text mutation.

### Data ingestion

1.1 Data is read from the input corpus, one document at a time. Currently only one corpus at a time is supported but easy
to support multiple ones. I store a single row identifying the corpus into the persistence layer.

1.2 For each document, it is first broken up into paragraphs. For each document, a record is stored into persistence.

1.3 For each paragraph, a record is stored into persistence. Paragraphs are broken up into sentences. OpenNLP is used
for sentence breaking. Included are the training file (./resources/sentences/dg-en-sent.train) and the respective
model (dg-en-sent.bin).

1.4 Each sentence is broken up into words. For each sentence, its type is also identified. E.g. "A walk in the park?" is
distinguished from "A walk in the park!" to later be able to preserve some punctuation.

1.5 Words are grouped into N-grams of configured cardinality. They're stored into the persistence layer as mappings of
tuples to N-gram endings. A tuple is everything in the N-gram minus the last token/word. The ending is the last part.

### Persistence

2.1 There are 2 storage providers, MockAccumulo-based and in-memory map based.

2.2 Current storage schema is as below, and can probably be optimized.

Corpora table:
family: corpus name
qualifier: corpus file location
value: corpus ID

Documents table:
family: corpus ID
qualifier: document name
value: document ID

Paragraphs table:
family: document ID
qualifier: paragraph ordinal number in the document
value: paragraph ID

Sentences table:
family: paragraph ID
qualifier: sentence ID
value: sentence type (based on starter/closer e.g. "....?" vs. "....!")

Ngrams table:
family: sentence ID
qualifier: the tuple
value: the matching ending

### Data retrieval / Text mutation

3.1 In a given corpus, we first pick a random document to start with.

3.2 Within that document, we randomly pick a paragraph to start with.

3.3 Within that paragraph, we pick a random sentence to start with.

3.4 Within that sentence, we pick an ngram to start with.

3.5 We then proceed following the ngrams as described in the Kata. There are provisions implemented for picking
a random match for a given tuple out of M possible matches within the persistence store. They can be picked
randomly or using a weighted random algorithm where preference tends to be given to matching endings which have
higher frequency of occurring for a given tuple within the corpus.

3.6 Based on the sentence type, an opener may be printed for a given mutated sentence. E.g. a sentence may start
with a quote, or with no opener. Once the desired number of words has been generated for the sentence, a sentence
closer may be printed, based on the sentence type stored in the Sentences table for that sentence, so we end up
with something like "A walk in the park!" with quotes or e.g. A walk in the park.

## TODO's, improvements, enhancements

1. More unit tests.
2. Taking better advantage of Accumulo capabilities such as:
  1. iterators, e.g. a summing iterator to calculate frequencies of ngram suffixes on the fly
  2. rehash the schema for better ways to store/retrieve ngrams to minimize scanning of records
3. Better/more sophisticated handling of:
  1. punctuation, including sentences such as "A walk in the park," - he said.
  2. capitalization
4. Handle multiple corpora (currently only really handles one corpus).

## Test results

See RESULTS.txt.
