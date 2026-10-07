============================================================
TEXT HACK - ADVANCED TEXT ANALYTICS ENGINE
============================================================

PROJECT IDENTITY
----------------
This is a DSA-3 text-analytics system with an intelligent resource
scheduler. The user's problem is text processing; scheduling is the
system-level decision layer that decides when and where the algorithmic
workloads execute.

CORE USER FLOW
--------------
TXT corpus -> workload analysis -> user query -> task creation
-> dependency DAG -> intelligent scheduling -> real algorithm
-> result -> measured runtime -> learned runtime profile -> history

10-DOCUMENT CURATED MULTILINGUAL INDIAN-TOPIC CORPUS
--------------------------
D01 Operating Systems
D02 Computer Networks
D03 Data Structures and Algorithms
D04 Indian Space Research
D05 Database Systems
D06 Telugu Language and Literature
D07 Indian Classical Dance
D08 Indian Constitution
D09 Indian Rivers and Geography
D10 Artificial Intelligence in India

The application creates any missing D01-D10 files automatically.
User-added TXT files are also supported.

TEXT QUERY MODULES
------------------
1. Exact pattern search       -> KMP
2. Fast substring search      -> Rabin-Karp
3. Fuzzy document matching    -> Edit Distance / DP
4. Pattern structure analysis -> Z Algorithm
5. Text indexing / document similarity -> Suffix Array + LCP
6. Document sequence alignment-> Dynamic Programming

SCHEDULING MODULE
-----------------
Runtime-aware HEFT V3 is an internal scheduling heuristic. It:
- creates runnable tasks from the selected text workflow
- respects the dependency DAG
- ranks ready work by remaining downstream work
- checks CPU/RAM compatibility
- selects a compatible resource using predicted finish time
- executes independent tasks in parallel
- measures actual runtime
- updates runtime estimates for future decisions
- records completed, failed and blocked tasks

LOGICAL RESOURCES
-----------------
R01 High Performance Worker : 4 CPU, 4096 MB
R02 Balanced Worker         : 2 CPU, 2048 MB
R03 Lightweight Worker      : 1 CPU, 1024 MB

These are modeled heterogeneous scheduler resources. The host/JVM
snapshot is displayed separately and is not falsely presented as the
worker capacity.

ADVANCED DSA-3 COVERAGE
-----------------------
- String matching: KMP, Z, Rabin-Karp
- Hashing: polynomial rolling hash in Rabin-Karp
- Suffix structures: suffix array + LCP
- Dynamic programming: edit distance and sequence alignment
- Network flow: Edmonds-Karp citation-flow demonstration
- Approximation/scheduling: HEFT-based resource scheduling
- Randomized algorithms: Miller-Rabin primality testing
- Parallelism: independent DAG tasks execute concurrently

FILE HANDLING
-------------
Documents are stored in documents/<DocumentID>.txt and contain:
ID, TITLE, AUTHOR, CATEGORY and CONTENT.
Algorithms process CONTENT only.
Request history is stored in results/request_history.txt.

USER-FRIENDLY OUTPUT
--------------------
The normal console hides low-level worker logs. A user sees:
request -> selected algorithm -> workload -> scheduling decision
-> clear result.
For pattern searches the verdict is explicitly FOUND or NOT FOUND,
with occurrence count and positions when available.

IMPORTANT ECLIPSE RUN TARGET
----------------------------
Run:
    src/test/TextProcessingPipelineDemo.java

Java class:
    test.TextProcessingPipelineDemo

FinalProjectDemo remains as a compatibility launcher.

RECOMMENDED DEMO
----------------
1. Browse document library and show the 10 curated documents.
2. Inspect one document to show file handling + workload estimation.
3. Search an existing phrase using KMP and show FOUND + positions.
4. Search a missing phrase and show NOT FOUND.
5. Compare two documents with Edit Distance and show similarity.
6. Run the complete pipeline and show parallel rounds/resource choices.
7. Open scheduling dashboard and explain HEFT's role in one minute.
8. Run citation flow to demonstrate Edmonds-Karp.
9. Run Miller-Rabin on 2147483647.
10. Show request history as evidence of persistent execution records.

KEY VIVA ANSWER: WHY HEFT?
---------------------------
The project is not a HEFT project. It is a text-processing pipeline
system. Each user request becomes a computational task with different
resource needs and dependencies. HEFT V3 is the scheduling layer that
chooses the execution order and compatible heterogeneous resource.
Therefore the text algorithms solve the user's problem, while the
scheduler optimizes execution of those workloads.

KEY VIVA ANSWER: WHAT MAKES IT INTELLIGENT?
--------------------------------------------
The system does not use one hard-coded order for every request. It
builds work dynamically, respects dependencies, checks resources,
uses runtime-aware ranking and predicted finish time, executes tasks
in parallel, measures actual execution time and updates future runtime
estimates.

ENGINE CONSTRAINT
-----------------
java.util.* is not used inside the algorithm/scheduling engine.
Algorithms and core structures are manually implemented.
============================================================


TEXT HACK UI
------------
The primary launcher now presents the project as an advanced-algorithm
text analytics engine. The core scheduler remains underneath the user
workflow rather than being presented as the entire product.

Main capabilities exposed in the console:
- Pattern Search: KMP, Rabin-Karp, Z Algorithm and live comparison
- Fuzzy Matching: Edit Distance / Dynamic Programming
- Document Similarity: Suffix Array + LCP
- Sequence Alignment: Dynamic Programming
- Citation Flow: Edmonds-Karp / Max Flow
- Intelligent Pipeline: dependency-aware HEFT V3 scheduling
- Miller-Rabin randomized primality testing
- DSA-3 algorithm map and system architecture view
- Corpus browsing and user TXT ingestion
- Persistent request history

RUN FROM TERMINAL
-----------------
    ./run.sh

OR FROM ECLIPSE
---------------
Run: test.TextProcessingPipelineDemo


WEB APPLICATION
----------------
Run on Mac: ./RUN_WEB_MAC.sh
Run on Windows: RUN_WEB_WINDOWS.bat
Then open: http://localhost:8000

The web UI is input-driven. Users can select any available document, choose comparison documents, enter their own patterns, select algorithms, run the six-stage intelligent pipeline, inspect runtime/resource assignments, view history, and upload .txt/.md/.csv documents. Uploaded documents receive U01, U02, ... IDs and immediately appear in every document selector.

IMPORTANT
---------
Do not run `cd IntelligentInformationPlanner_15_10_FINAL` after you are already inside that folder. From the project folder, run:
  chmod +x RUN_WEB_MAC.sh
  ./RUN_WEB_MAC.sh

If permission is still denied, use:
  bash RUN_WEB_MAC.sh
