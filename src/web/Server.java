package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import algorithms.KMP;
import algorithms.RabinKarp;
import algorithms.ZAlgorithm;
import algorithms.EditDistance;
import algorithms.SequenceAlignment;
import algorithms.SuffixArray;
import algorithms.EdmondsKarp;
import algorithms.MillerRabin;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TEXT HACK web adapter.
 * The web layer calls the project's existing DSA implementations and exposes
 * them through a browser UI. Uploaded text documents are persisted in /documents.
 */
public class Server {
    static final int PORT = 8000;
    static final Path DOCUMENT_DIR = Paths.get("documents").toAbsolutePath().normalize();
    static final Map<String,String> DOCS = new LinkedHashMap<>();
    static final Map<String,String> NAMES = new LinkedHashMap<>();
    static final List<Map<String,String>> HISTORY = new ArrayList<>();
    static final AtomicInteger UPLOAD_COUNTER = new AtomicInteger(1);

    static final ResourceSpec[] RESOURCES = {
        new ResourceSpec("R01", "High Performance Worker", 4, 4096),
        new ResourceSpec("R02", "Balanced Worker", 2, 2048),
        new ResourceSpec("R03", "Lightweight Worker", 1, 1024)
    };

    static {
        try { loadDocuments(); } catch (Exception ignored) {}
    }

    public static void main(String[] args) throws Exception {
        loadDocuments();
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/health", Server::health);
        server.createContext("/api/system", Server::system);
        server.createContext("/api/algorithms", Server::algorithms);
        server.createContext("/api/corpus", Server::corpus);
        server.createContext("/api/document", Server::document);
        server.createContext("/api/upload", Server::upload);
        server.createContext("/api/resources", Server::resources);
        server.createContext("/api/run", Server::run);
        server.createContext("/api/pipeline", Server::pipeline);
        server.createContext("/api/compare", Server::compare);
        server.createContext("/api/history", Server::history);
        server.createContext("/", Server::staticFiles);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("============================================================");
        System.out.println("TEXT HACK WEB BACKEND");
        System.out.println("Running at http://localhost:" + PORT);
        System.out.println("Documents : " + DOCS.size());
        System.out.println("Workers   : 3");
        System.out.println("Algorithms: 8 executable + comparison/map tools");
        System.out.println("============================================================");
    }

    static synchronized void loadDocuments() throws IOException {
        Files.createDirectories(DOCUMENT_DIR);
        DOCS.clear(); NAMES.clear();
        List<Path> files = new ArrayList<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(DOCUMENT_DIR, "*.txt")) {
            for (Path p : ds) if (Files.isRegularFile(p)) files.add(p);
        }
        files.sort(Comparator.comparing(p -> p.getFileName().toString()));
        int maxUpload = 0;
        for (Path p : files) {
            String filename = p.getFileName().toString();
            String id = filename.substring(0, filename.length()-4);
            String text = extractContent(Files.readString(p, StandardCharsets.UTF_8));
            if (text == null) text = "";
            DOCS.put(id, text);
            NAMES.put(id, filename);
            if (id.matches("U\\d+")) maxUpload = Math.max(maxUpload, Integer.parseInt(id.substring(1)));
        }
        UPLOAD_COUNTER.set(maxUpload + 1);
    }

    static String extractContent(String raw) {
        if (raw == null) return "";
        int marker = raw.indexOf("CONTENT:");
        if (marker >= 0) return raw.substring(marker + "CONTENT:".length()).trim();
        return raw.trim();
    }

    static void health(HttpExchange e) throws IOException {
        json(e, "{\"status\":\"UP\",\"service\":\"TEXT HACK Java Backend\"}");
    }

    static void system(HttpExchange e) throws IOException {
        json(e, "{\"name\":\"TEXT HACK\",\"status\":\"READY\",\"documents\":"+DOCS.size()+",\"workers\":3,\"algorithms\":8,\"pipelineTasks\":6,\"uploadEnabled\":true}");
    }

    static void algorithms(HttpExchange e) throws IOException {
        json(e, "[{"+
            "\"id\":\"kmp\",\"name\":\"KMP\",\"group\":\"Pattern Search\",\"input\":\"document-pattern\"},{"+
            "\"id\":\"rabin-karp\",\"name\":\"Rabin-Karp\",\"group\":\"Pattern Search\",\"input\":\"document-pattern\"},{"+
            "\"id\":\"z\",\"name\":\"Z Algorithm\",\"group\":\"Pattern Analysis\",\"input\":\"document-pattern\"},{"+
            "\"id\":\"suffix-lcp\",\"name\":\"Suffix Array + LCP\",\"group\":\"Document Similarity\",\"input\":\"two-documents\"},{"+
            "\"id\":\"edit-distance\",\"name\":\"Edit Distance\",\"group\":\"Fuzzy Matching\",\"input\":\"two-documents\"},{"+
            "\"id\":\"alignment\",\"name\":\"Sequence Alignment\",\"group\":\"Alignment\",\"input\":\"two-documents\"},{"+
            "\"id\":\"edmonds-karp\",\"name\":\"Edmonds-Karp\",\"group\":\"Graph Optimization\",\"input\":\"flow-network\"},{"+
            "\"id\":\"miller-rabin\",\"name\":\"Miller-Rabin\",\"group\":\"Randomized / Advanced\",\"input\":\"number\"}]");
    }

    static void corpus(HttpExchange e) throws IOException {
        StringBuilder b = new StringBuilder("[");
        int i = 0;
        for (Map.Entry<String,String> x : DOCS.entrySet()) {
            if (i++ > 0) b.append(',');
            String t = x.getValue();
            b.append("{\"id\":\"").append(esc(x.getKey())).append("\",\"name\":\"")
             .append(esc(NAMES.getOrDefault(x.getKey(), "Document " + x.getKey())))
             .append("\",\"characters\":").append(t.length())
             .append(",\"words\":").append(words(t))
             .append(",\"lines\":").append(lines(t))
             .append(",\"cpu\":").append(cpu(t))
             .append(",\"ram\":").append(ram(t))
             .append(",\"uploaded\":").append(x.getKey().startsWith("U"))
             .append("}");
        }
        json(e, b.append(']').toString());
    }

    static void document(HttpExchange e) throws IOException {
        Map<String,String> q = query(e.getRequestURI().getRawQuery());
        String id = q.getOrDefault("id", "D01");
        String text = DOCS.get(id);
        if (text == null) { jsonError(e, 404, "Document not found: " + id); return; }
        json(e, "{\"id\":\""+esc(id)+"\",\"name\":\""+esc(NAMES.getOrDefault(id,id))+"\",\"content\":\""+esc(text)+"\"}");
    }

    static void upload(HttpExchange e) throws IOException {
        if (!"POST".equalsIgnoreCase(e.getRequestMethod())) { jsonError(e, 405, "POST required"); return; }
        String ct = e.getRequestHeaders().getFirst("Content-Type");
        if (ct == null || !ct.toLowerCase(Locale.ROOT).contains("multipart/form-data")) { jsonError(e, 400, "Use multipart/form-data"); return; }
        String boundary = null;
        for (String part : ct.split(";")) {
            part = part.trim();
            if (part.startsWith("boundary=")) boundary = part.substring("boundary=".length()).replace("\"", "");
        }
        if (boundary == null) { jsonError(e, 400, "Missing multipart boundary"); return; }
        byte[] body = readAll(e.getRequestBody());
        MultipartPart part = parseFilePart(body, boundary);
        if (part == null || part.data.length == 0) { jsonError(e, 400, "No document file received"); return; }
        String lower = part.filename.toLowerCase(Locale.ROOT);
        if (!(lower.endsWith(".txt") || lower.endsWith(".md") || lower.endsWith(".csv"))) {
            jsonError(e, 400, "For DSA text processing, upload a .txt, .md or .csv file."); return;
        }
        String text = new String(part.data, StandardCharsets.UTF_8).trim();
        if (text.isEmpty()) { jsonError(e, 400, "Uploaded document is empty"); return; }
        synchronized (Server.class) {
            String id;
            do { id = String.format(Locale.US, "U%02d", UPLOAD_COUNTER.getAndIncrement()); } while (DOCS.containsKey(id));
            String safeName = sanitizeFilename(part.filename);
            Path out = DOCUMENT_DIR.resolve(id + ".txt");
            Files.writeString(out, text + System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            DOCS.put(id, text); NAMES.put(id, safeName);
            json(e, "{\"success\":true,\"id\":\""+id+"\",\"name\":\""+esc(safeName)+"\",\"characters\":"+text.length()+",\"words\":"+words(text)+"}");
        }
    }

    static void resources(HttpExchange e) throws IOException {
        StringBuilder b = new StringBuilder("[");
        for (int i=0;i<RESOURCES.length;i++) {
            if (i>0) b.append(','); ResourceSpec r=RESOURCES[i];
            b.append("{\"id\":\"").append(r.id).append("\",\"name\":\"").append(r.name)
             .append("\",\"cpu\":").append(r.cpu).append(",\"ram\":").append(r.ram).append(",\"status\":\"AVAILABLE\"}");
        }
        json(e,b.append(']').toString());
    }

    static void run(HttpExchange e) throws IOException {
        Map<String,String> q=query(e.getRequestURI().getRawQuery());
        String a=q.getOrDefault("algorithm","kmp");
        long st=System.nanoTime();
        String out;
        try {
            switch(a) {
                case "kmp": out = patternResult("KMP", q, new Searcher(){public int[] go(String t,String p){return new KMP().search(t,p);}}); break;
                case "rabin-karp": out = patternResult("Rabin-Karp", q, new Searcher(){public int[] go(String t,String p){return new RabinKarp().search(t,p);}}); break;
                case "z": out = patternResult("Z Algorithm", q, new Searcher(){public int[] go(String t,String p){return new ZAlgorithm().search(t,p);}}); break;
                case "edit-distance": out=editResult(q); break;
                case "alignment": out=alignmentResult(q); break;
                case "suffix-lcp": out=suffixResult(q); break;
                case "miller-rabin": {
                    long n=Long.parseLong(q.getOrDefault("number","3"));
                    out="{\"algorithm\":\"Miller-Rabin\",\"number\":"+n+",\"result\":\""+esc(new MillerRabin().classify(n))+"\"}"; break;
                }
                case "edmonds-karp": {
                    int[][] g={{0,8,5,0,0,0},{0,0,3,4,0,0},{0,0,0,0,7,0},{0,0,2,0,2,5},{0,0,0,0,0,6},{0,0,0,0,0,0}};
                    EdmondsKarp.FlowResult r=new EdmondsKarp().maxFlow(g,0,5);
                    out="{\"algorithm\":\"Edmonds-Karp\",\"maxFlow\":"+r.getMaxFlow()+",\"augmentingPaths\":"+r.getAugmentingPaths()+"}"; break;
                }
                default: throw new IllegalArgumentException("Unknown algorithm: "+a);
            }
            String id=q.getOrDefault("document", q.getOrDefault("primary", ""));
            addHistory(a, id, q.getOrDefault("secondDocument", q.getOrDefault("comparison","")), summaryFor(out));
        } catch(Exception ex) { out="{\"error\":\""+esc(ex.getMessage()==null?ex.toString():ex.getMessage())+"\"}"; }
        out=appendRuntime(out,(System.nanoTime()-st)/1e6);
        json(e,out);
    }

    static String patternResult(String name, Map<String,String> q, Searcher searcher) {
        String d=requireDoc(q.getOrDefault("document","")); String p=q.getOrDefault("pattern","");
        if (p.trim().isEmpty()) throw new IllegalArgumentException("Enter a pattern before running the algorithm.");
        int[] r=searcher.go(DOCS.get(d),p);
        return "{\"algorithm\":\""+name+"\",\"document\":\""+d+"\",\"documentName\":\""+esc(NAMES.get(d))+"\",\"pattern\":\""+esc(p)+"\",\"matches\":"+arr(r)+",\"count\":"+r.length+",\"status\":\""+(r.length==0?"NOT FOUND":"MATCHES FOUND")+"\"}";
    }

    static String editResult(Map<String,String> q) {
        String a=requireDoc(q.getOrDefault("document", q.getOrDefault("primary","")));
        String b=requireDoc(q.getOrDefault("secondDocument", q.getOrDefault("comparison","")));
        int d=new EditDistance().calculate(DOCS.get(a),DOCS.get(b));
        return "{\"algorithm\":\"Edit Distance\",\"primary\":\""+a+"\",\"comparison\":\""+b+"\",\"distance\":"+d+",\"similarity\":"+fmt(similarity(DOCS.get(a),DOCS.get(b),d))+"}";
    }
    static String alignmentResult(Map<String,String> q) {
        String a=requireDoc(q.getOrDefault("document", q.getOrDefault("primary","")));
        String b=requireDoc(q.getOrDefault("secondDocument", q.getOrDefault("comparison","")));
        int score=new SequenceAlignment().getAlignmentScore(DOCS.get(a),DOCS.get(b));
        return "{\"algorithm\":\"Sequence Alignment\",\"primary\":\""+a+"\",\"comparison\":\""+b+"\",\"score\":"+score+"}";
    }
    static String suffixResult(Map<String,String> q) {
        String a=requireDoc(q.getOrDefault("document", q.getOrDefault("primary","")));
        String b=requireDoc(q.getOrDefault("secondDocument", q.getOrDefault("comparison","")));
        SuffixArray sa=new SuffixArray(); int[] x=sa.buildSuffixArray(DOCS.get(b)); int[] l=sa.buildLCP(DOCS.get(b),x);
        int max=0; for(int v:l) if(v>max) max=v;
        double pct=DOCS.get(b).isEmpty()?0:(100.0*max/Math.max(1,DOCS.get(a).length()));
        return "{\"algorithm\":\"Suffix Array + LCP\",\"primary\":\""+a+"\",\"comparison\":\""+b+"\",\"suffixes\":"+x.length+",\"lcpEntries\":"+l.length+",\"maxLCP\":"+max+",\"similarityEstimate\":"+fmt(pct)+"}";
    }

    static void pipeline(HttpExchange e) throws IOException {
        Map<String,String> q=query(e.getRequestURI().getRawQuery());
        String p=requireDoc(q.getOrDefault("primary","")); String c=requireDoc(q.getOrDefault("comparison","")); String pat=q.getOrDefault("pattern","");
        if(pat.trim().isEmpty()) { jsonError(e,400,"Enter a pattern before running the pipeline."); return; }
        long wall=System.nanoTime();
        PipelineTask[] tasks={
            new PipelineTask("T01","Exact Pattern Search","KMP",p,pat,1,512,1,new String[]{}),
            new PipelineTask("T02","Keyword Verification","Rabin-Karp",p,pat,1,512,1,new String[]{}),
            new PipelineTask("T03","Pattern Structure Analysis","Z Algorithm",p,pat,1,512,1,new String[]{}),
            new PipelineTask("T04","Text Index Construction","Suffix Array + LCP",c,"",2,1536,2,new String[]{"T01","T02","T03"}),
            new PipelineTask("T05","Fuzzy Document Similarity","Edit Distance",p,c,2,1536,2,new String[]{"T03"}),
            new PipelineTask("T06","Cross-Document Sequence Alignment","Sequence Alignment",p,c,2,1536,2,new String[]{"T04","T05"})
        };
        Map<String,String> resultByTask=new HashMap<>();
        int completed=0, rounds=0;
        Map<String,Double> learned=new HashMap<>();
        double[] resourceReady={0,0,0};
        List<Map<String,Object>> allRows=new ArrayList<>();
        ExecutorService pool=Executors.newFixedThreadPool(3);
        try {
            while(completed<tasks.length) {
                rounds++;
                List<PipelineTask> ready=new ArrayList<>();
                for(PipelineTask t:tasks) if(!t.done && dependenciesDone(t,tasks,resultByTask)) ready.add(t);
                if(ready.isEmpty()) throw new IllegalStateException("Dependency graph is blocked or cyclic.");
                ready.sort((a,b)->Double.compare(rank(b,tasks,learned),rank(a,tasks,learned)));
                int slots=Math.min(RESOURCES.length,ready.size());
                List<PipelineTask> selected=ready.subList(0,slots);
                List<Future<Map<String,Object>>> futures=new ArrayList<>();
                boolean[] usedResource=new boolean[RESOURCES.length];
                for(PipelineTask t:selected) {
                    int ri=chooseResource(t,resourceReady,learned,usedResource);
                    usedResource[ri]=true;
                    t.resource=RESOURCES[ri].id;
                    double predicted=Math.max(resourceReady[ri],0)+estimate(t,learned)/Math.max(1,RESOURCES[ri].cpu);
                    resourceReady[ri]=predicted;
                    final PipelineTask ft=t;
                    futures.add(pool.submit(()->executePipelineTask(ft,p,c,pat)));
                }
                for(int i=0;i<futures.size();i++) {
                    Map<String,Object> row=futures.get(i).get(); PipelineTask t=selected.get(i);
                    t.done=true; completed++; resultByTask.put(t.id,(String)row.get("result"));
                    double actual=Double.parseDouble(String.valueOf(row.get("runtimeMs")));
                    Double old=learned.get(t.algorithm);
                    learned.put(t.algorithm,old==null?actual:0.7*old+0.3*actual);
                    row.put("round",rounds); row.put("resource",t.resource); row.put("rank",fmt(rank(t,tasks,learned)));
                    allRows.add(row);
                }
            }
        } catch(Exception ex) {
            pool.shutdownNow(); jsonError(e,500,"Pipeline execution failed: "+ex.getMessage()); return;
        } finally { pool.shutdown(); }
        double makespan=(System.nanoTime()-wall)/1e6;
        double throughput=makespan<=0?0:completed/(makespan/1000.0);
        StringBuilder rows=new StringBuilder("[");
        for(int i=0;i<allRows.size();i++){ if(i>0)rows.append(','); rows.append(mapJson(allRows.get(i))); }
        rows.append(']');
        json(e,"{\"primaryDocument\":\""+p+"\",\"comparisonDocument\":\""+c+"\",\"pattern\":\""+esc(pat)+"\",\"tasks\":"+rows+",\"completed\":"+completed+",\"total\":6,\"failed\":0,\"blocked\":0,\"rounds\":"+rounds+",\"makespan\":"+fmt(makespan)+",\"throughput\":"+fmt(throughput)+",\"status\":\"PIPELINE COMPLETED SUCCESSFULLY\",\"scheduler\":\"Runtime-aware HEFT V3\"}");
    }

    static Map<String,Object> executePipelineTask(PipelineTask t,String primary,String comparison,String pattern) {
        long s=System.nanoTime(); String result;
        try {
            if(t.algorithm.equals("KMP")){int n=new KMP().search(DOCS.get(primary),pattern).length; result=n==0?"NOT FOUND":n+" occurrence(s)";}
            else if(t.algorithm.equals("Rabin-Karp")){int n=new RabinKarp().search(DOCS.get(primary),pattern).length; result=n==0?"NOT FOUND":n+" occurrence(s)";}
            else if(t.algorithm.equals("Z Algorithm")){int n=new ZAlgorithm().search(DOCS.get(primary),pattern).length; result=n==0?"NOT FOUND":n+" occurrence(s)";}
            else if(t.algorithm.equals("Suffix Array + LCP")){SuffixArray sa=new SuffixArray(); int[] x=sa.buildSuffixArray(DOCS.get(comparison)); sa.buildLCP(DOCS.get(comparison),x); result="COMPLETED · "+x.length+" suffixes";}
            else if(t.algorithm.equals("Edit Distance")){int d=new EditDistance().calculate(DOCS.get(primary),DOCS.get(comparison)); result="Distance="+d+" · Similarity="+fmt(similarity(DOCS.get(primary),DOCS.get(comparison),d))+"%";}
            else {int score=new SequenceAlignment().getAlignmentScore(DOCS.get(primary),DOCS.get(comparison)); result="SCORE "+score;}
        } catch(Exception ex){ result="ERROR: "+ex.getMessage(); }
        Map<String,Object> m=new LinkedHashMap<>(); m.put("stage",t.id.substring(1)); m.put("stageName",t.name); m.put("algorithm",t.algorithm); m.put("cpu",t.cpu); m.put("result",result); m.put("runtimeMs",fmt((System.nanoTime()-s)/1e6)); return m;
    }

    static double rank(PipelineTask t,PipelineTask[] all,Map<String,Double> learned){
        double own=estimate(t,learned); double max=0;
        for(PipelineTask x:all) if(dependsOn(t,x)) max=Math.max(max,rank(x,all,learned));
        return own+max;
    }
    static boolean dependsOn(PipelineTask t,PipelineTask x){for(String d:t.deps)if(d.equals(x.id))return true;return false;}
    static boolean dependenciesDone(PipelineTask t,PipelineTask[] all,Map<String,String> results){for(String d:t.deps)if(!results.containsKey(d))return false;return true;}
    static double estimate(PipelineTask t,Map<String,Double> learned){Double v=learned.get(t.algorithm);return v==null?t.baseMs:v;}
    static int chooseResource(PipelineTask t,double[] ready,Map<String,Double> learned,boolean[] used){
        int best=-1; double bestFinish=Double.MAX_VALUE;
        for(int i=0;i<RESOURCES.length;i++) if(!used[i] && t.cpu<=RESOURCES[i].cpu && t.ram<=RESOURCES[i].ram){double finish=ready[i]+estimate(t,learned)/RESOURCES[i].cpu;if(finish<bestFinish){bestFinish=finish;best=i;}}
        if(best<0) throw new IllegalArgumentException("No resource can satisfy "+t.id);
        return best;
    }

    static void compare(HttpExchange e)throws IOException{
        Map<String,String>q=query(e.getRequestURI().getRawQuery()); String d=requireDoc(q.getOrDefault("document","")); String p=q.getOrDefault("pattern",""); if(p.trim().isEmpty()){jsonError(e,400,"Enter a pattern.");return;}
        String[] ids={"kmp","rabin-karp","z"}; String[] names={"KMP","Rabin-Karp","Z Algorithm"}; StringBuilder b=new StringBuilder("[");
        for(int i=0;i<ids.length;i++){if(i>0)b.append(',');long s=System.nanoTime();int n=ids[i].equals("kmp")?new KMP().search(DOCS.get(d),p).length:ids[i].equals("rabin-karp")?new RabinKarp().search(DOCS.get(d),p).length:new ZAlgorithm().search(DOCS.get(d),p).length;b.append("{\"algorithm\":\"").append(names[i]).append("\",\"matches\":").append(n).append(",\"runtimeMs\":").append(fmt((System.nanoTime()-s)/1e6)).append("}");}
        json(e,b.append(']').toString());
    }

    static synchronized void addHistory(String algorithm,String a,String b,String result){Map<String,String> h=new LinkedHashMap<>();h.put("algorithm",pretty(algorithm));h.put("documents",a+(b==null||b.isEmpty()?"":" vs "+b));h.put("result",result);h.put("time",new java.text.SimpleDateFormat("HH:mm:ss").format(new Date()));HISTORY.add(0,h);if(HISTORY.size()>50)HISTORY.remove(HISTORY.size()-1);}
    static String summaryFor(String json){return json.replaceAll("[{}\\\"\\[\\]]","").replaceAll("\\\\s+"," ").trim();}
    static void history(HttpExchange e)throws IOException{StringBuilder b=new StringBuilder("{\"requests\":"+HISTORY.size()+",\"recent\":[");for(int i=0;i<HISTORY.size()&&i<20;i++){if(i>0)b.append(',');Map<String,String>h=HISTORY.get(i);b.append("{\"algorithm\":\"").append(esc(h.get("algorithm"))).append("\",\"documents\":\"").append(esc(h.get("documents"))).append("\",\"result\":\"").append(esc(h.get("result"))).append("\",\"time\":\"").append(h.get("time")).append("\"}");}json(e,b.append("]}").toString());}

    static void staticFiles(HttpExchange e)throws IOException{
        String p=e.getRequestURI().getPath();if(p.equals("/"))p="/index.html";Path root=Paths.get("frontend").toAbsolutePath().normalize();Path f=root.resolve(p.substring(1)).normalize();if(!f.startsWith(root)||!Files.exists(f)||Files.isDirectory(f)){response(e,404,"Not Found","text/plain");return;}byte[] b=Files.readAllBytes(f);responseBytes(e,200,b,type(f.toString()));
    }

    static String requireDoc(String id){if(id==null||id.trim().isEmpty())throw new IllegalArgumentException("Select a document.");if(!DOCS.containsKey(id))throw new IllegalArgumentException("Document not found: "+id);return id;}
    static Map<String,String> query(String raw){Map<String,String>m=new HashMap<>();if(raw==null)return m;for(String s:raw.split("&")){String[]x=s.split("=",2);m.put(dec(x[0]),x.length>1?dec(x[1]):"");}return m;}
    static String dec(String s){return URLDecoder.decode(s,StandardCharsets.UTF_8);}
    static int words(String s){String x=s.trim();return x.isEmpty()?0:x.split("\\s+").length;}
    static int lines(String s){return s.isEmpty()?0:s.split("\\R",-1).length;}
    static int cpu(String s){return s.length()>500?2:1;}
    static int ram(String s){return Math.max(256,Math.min(4096,256+s.length()*2));}
    static double similarity(String a,String b,int d){int m=Math.max(a.length(),b.length());return m==0?100:Math.max(0,(1-(double)d/m)*100);}
    static String arr(int[]a){StringBuilder b=new StringBuilder("[");for(int i=0;i<a.length;i++){if(i>0)b.append(',');b.append(a[i]);}return b.append(']').toString();}
    static String fmt(double x){return String.format(Locale.US,"%.2f",x);}
    static String esc(String s){return s==null?"":s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r").replace("\t","\\t");}
    static String appendRuntime(String json,double ms){if(!json.endsWith("}"))return json;return json.substring(0,json.length()-1)+",\"actualRuntimeMs\":"+fmt(ms)+"}";}
    static void json(HttpExchange e,String s)throws IOException{response(e,200,s,"application/json; charset=UTF-8");}
    static void jsonError(HttpExchange e,int code,String msg)throws IOException{response(e,code,"{\"error\":\""+esc(msg)+"\"}","application/json; charset=UTF-8");}
    static void response(HttpExchange e,int code,String s,String type)throws IOException{responseBytes(e,code,s.getBytes(StandardCharsets.UTF_8),type);}
    static void responseBytes(HttpExchange e,int code,byte[] b,String type)throws IOException{e.getResponseHeaders().set("Content-Type",type);e.getResponseHeaders().set("Access-Control-Allow-Origin","*");e.getResponseHeaders().set("Access-Control-Allow-Headers","Content-Type");e.getResponseHeaders().set("Access-Control-Allow-Methods","GET,POST,OPTIONS");e.sendResponseHeaders(code,b.length);try(OutputStream o=e.getResponseBody()){o.write(b);}}
    static String type(String p){if(p.endsWith(".html"))return"text/html; charset=UTF-8";if(p.endsWith(".css"))return"text/css; charset=UTF-8";if(p.endsWith(".js"))return"application/javascript; charset=UTF-8";return"application/octet-stream";}
    static byte[] readAll(InputStream in)throws IOException{ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] buf=new byte[8192];int n;while((n=in.read(buf))!=-1)out.write(buf,0,n);return out.toByteArray();}
    static MultipartPart parseFilePart(byte[] body,String boundary)throws IOException{byte[] marker=("--"+boundary).getBytes(StandardCharsets.ISO_8859_1);int pos=indexOf(body,marker,0);while(pos>=0){int headStart=pos+marker.length;int next=indexOf(body,marker,headStart);if(next<0)break;int hEnd=indexOf(body,new byte[]{13,10,13,10},headStart);if(hEnd>0&&hEnd<next){String headers=new String(body,headStart,hEnd-headStart,StandardCharsets.ISO_8859_1);int dataStart=hEnd+4;int dataEnd=next-2;if(headers.toLowerCase(Locale.ROOT).contains("filename=")){String fn=extractFilename(headers);if(fn!=null)return new MultipartPart(fn,Arrays.copyOfRange(body,dataStart,Math.max(dataStart,dataEnd)));} }pos=next;}return null;}
    static String extractFilename(String headers){int p=headers.indexOf("filename=");if(p<0)return null;String x=headers.substring(p+9).trim();if(x.startsWith("\"")){int q=x.indexOf('"',1);return q>0?x.substring(1,q):x.substring(1);}int semi=x.indexOf(';');return semi>0?x.substring(0,semi):x;}
    static int indexOf(byte[] data,byte[] target,int from){outer:for(int i=Math.max(0,from);i<=data.length-target.length;i++){for(int j=0;j<target.length;j++)if(data[i+j]!=target[j])continue outer;return i;}return -1;}
    static String sanitizeFilename(String s){if(s==null||s.trim().isEmpty())return"uploaded-document.txt";return Paths.get(s).getFileName().toString().replaceAll("[^a-zA-Z0-9._-]","_");}
    static String pretty(String s){if(s==null)return"";if(s.equals("rabin-karp"))return"Rabin-Karp";if(s.equals("edit-distance"))return"Edit Distance";if(s.equals("suffix-lcp"))return"Suffix Array + LCP";if(s.equals("alignment"))return"Sequence Alignment";if(s.equals("miller-rabin"))return"Miller-Rabin";if(s.equals("edmonds-karp"))return"Edmonds-Karp";if(s.equals("kmp"))return"KMP";if(s.equals("z"))return"Z Algorithm";return s;}
    static String mapJson(Map<String,Object> m){StringBuilder b=new StringBuilder("{");int i=0;for(Map.Entry<String,Object> e:m.entrySet()){if(i++>0)b.append(',');b.append("\"").append(esc(e.getKey())).append("\":");Object v=e.getValue();if(v instanceof Number)b.append(v);else b.append("\"").append(esc(String.valueOf(v))).append("\"");}return b.append('}').toString();}

    interface Searcher{int[] go(String text,String pattern);}
    static class MultipartPart{String filename;byte[] data;MultipartPart(String f,byte[]d){filename=f;data=d;}}
    static class ResourceSpec{String id,name;int cpu,ram;ResourceSpec(String i,String n,int c,int r){id=i;name=n;cpu=c;ram=r;}}
    static class PipelineTask{String id,name,algorithm,doc,second;int cpu,ram,baseMs;String pattern,resource;String[]deps;boolean done=false;PipelineTask(String i,String n,String a,String d,String s,int c,int r,int b,String[]de){id=i;name=n;algorithm=a;doc=d;second=s;pattern=s;cpu=c;ram=r;baseMs=b*10;deps=de;}}
}
