# IntelligentInformationPlanner

## INTELLIGENT RESOURCE SCHEDULING FOR TEXT PROCESSING PIPELINES

A simple-medium console PBL application that demonstrates how text-processing requests can be planned, assigned to processing resources, executed, monitored and measured.

### Main capabilities

- Document library management
- Text search
- Document comparison
- Document/content analysis
- Shared-content processing
- Multi-stage processing requests
- Dependency-aware task planning
- Priority-aware scheduling
- Resource-aware execution
- Failure recovery support
- Runtime and throughput measurement
- DSA/algorithm implementations kept inside the processing engine

### Console UI

The application starts with an **Intelligent Text Workspace** and groups features into:

- Workspace
- Scheduler
- Insights
- System

Technical algorithm names are intentionally kept inside the source code. Normal users see task names such as **Fast Text Search**, **Document Comparison**, **Resource Allocation**, and **Content Analysis**.

### Running in Eclipse

1. Import the `IntelligentInformationPlanner` folder as an existing Eclipse project.
2. Ensure the project uses the JRE configured by the existing `.classpath`.
3. Run `src/main/Main.java` as a Java application.
4. The `documents` folder is used as the file-based document library.

### Project structure

```text
IntelligentInformationPlanner/
  .classpath
  .project
  README.md
  documents/
  results/
  src/
    algorithms/       Existing DSA implementations
    execution/        Task execution, recovery and monitoring
    main/             Console application entry point
    model/            Document, Task and Resource models
    scheduling/       Workload analysis and scheduling
    storage/          File-based document management
    structures/       Existing graph and priority-queue structures
```

No external libraries are required by the project.
