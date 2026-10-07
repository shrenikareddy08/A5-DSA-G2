TEXT HACK — LIVE WEB APPLICATION
================================

WHAT THIS VERSION DOES
-----------------------
• Real browser UI backed by Java on localhost:8000
• Calls the existing project DSA implementations
• Any D01-D10 document can be selected in every applicable operation
• Any two documents can be compared
• User enters the pattern instead of relying on a hardcoded search term
• Algorithm Lab lets the user choose any executable algorithm
• Intelligent Pipeline executes the 6-stage dependency graph
• Runtime-aware HEFT V3 chooses logical resources dynamically
• Actual algorithm runtime is measured and displayed
• Results and API executions are stored in session history
• Upload .txt / .md / .csv documents; uploaded files become U01, U02, ...
• Uploaded documents immediately appear in all document selectors
• Corpus Explorer lets the user preview document content

RUN ON MAC
----------
From the project folder:
  chmod +x RUN_WEB_MAC.sh
  ./RUN_WEB_MAC.sh

If permission is denied:
  bash RUN_WEB_MAC.sh

Then open:
  http://localhost:8000

RUN IN ECLIPSE
--------------
Import the folder as an Existing Java Project.
Run:
  src/web/Server.java
Then open http://localhost:8000

INPUT FLOW
----------
USER INPUT → FRONTEND → JAVA API → EXISTING DSA ALGORITHM
→ HEFT V3 → LOGICAL RESOURCE → MEASURED RESULT → FRONTEND

SUPPORTED EXECUTABLE ALGORITHMS
--------------------------------
1. KMP
2. Rabin-Karp
3. Z Algorithm
4. Suffix Array + LCP
5. Edit Distance
6. Sequence Alignment
7. Edmonds-Karp
8. Miller-Rabin

PIPELINE DEPENDENCIES
---------------------
KMP, Rabin-Karp, Z → Round 1
Suffix Array + LCP depends on KMP + Rabin-Karp + Z
Edit Distance depends on Z
Sequence Alignment depends on Suffix Array + LCP + Edit Distance

NOTE
----
The three scheduler resources R01/R02/R03 are logical heterogeneous
resources modeled by the project. They are not three physical machines.
Energy consumption is not measured by the application.
