This project scrapes data from online education platforms (Coursera, edX, Stanford, etc.) and builds a mini search engine for the collected courses.
It combines web scraping with information retrieval techniques such as inverted indexing, ranking, and efficient search structures.

Web Scraping

Extracts course metadata: Title, Description, Instructor, Price, URL, Duration, Platform.

Handles both static and dynamic pages.

Saves all data in structured CSV format.

Inverted Indexing → maps words to course entries for fast retrieval.

Trie-based Prefix Search → enables efficient autocomplete & word lookups.

AVL Trees → ensures balanced storage of course metadata for quick lookups.

Heaps (Priority Queues) → used for ranking top-k results efficiently.

Word Frequency Analysis → used to compute TF (term frequency) for ranking.

Page Ranking → ranks courses based on relevance, popularity, or frequency.

Tech Stack

Python

BeautifulSoup4 / Requests / Selenium → web scraping

Pandas → data cleaning + CSV handling

Trie, AVL Tree, Heap → custom-built search structures

Inverted Index + Ranking → efficient search & relevance scoring

