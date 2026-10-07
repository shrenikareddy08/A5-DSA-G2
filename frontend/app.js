const $ = (s, root=document) => root.querySelector(s);
const content = $('#content');
let DOCS = [];
const titles = {
  dashboard:'Dashboard', lab:'Algorithm Lab', pipeline:'Intelligent Pipeline', documents:'Documents',
  scheduler:'Scheduler & Resources', history:'Results & History', map:'DSA-3 Algorithm Map', overview:'System Architecture'
};
const esc = s => String(s ?? '').replace(/[&<>'"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c]));
const label = a => ({'kmp':'KMP','rabin-karp':'Rabin-Karp','z':'Z Algorithm','suffix-lcp':'Suffix Array + LCP','edit-distance':'Edit Distance','alignment':'Sequence Alignment','edmonds-karp':'Edmonds-Karp','miller-rabin':'Miller-Rabin'}[a] || a);

async function api(path, options={}) {
  const response = await fetch(path, options);
  let data;
  try { data = await response.json(); } catch { throw new Error('The server returned an invalid response.'); }
  if (!response.ok || data.error) throw new Error(data.error || `Request failed (${response.status})`);
  return data;
}
function toast(message, error=false) {
  const t=$('#toast'); t.textContent=message; t.style.borderColor=error?'#713040':'#2d4667'; t.classList.add('show');
  clearTimeout(window.__toast); window.__toast=setTimeout(()=>t.classList.remove('show'),2600);
}
async function refreshDocs(){ DOCS=await api('/api/corpus'); $('#sideDocs').textContent=DOCS.length; return DOCS; }
function docOptions(selected='D01') { return DOCS.map(d=>`<option value="${esc(d.id)}" ${d.id===selected?'selected':''}>${esc(d.id)} · ${esc(d.name)}${d.uploaded?' · uploaded':''}</option>`).join(''); }
function docSelect(id,labelText,selected='D01'){ return `<div class="field"><label>${labelText}<select id="${id}">${docOptions(selected)}</select></label></div>`; }
function stats(items){ return `<div class="grid stats">${items.map(x=>`<div class="card stat-card"><div class="stat-label">${esc(x[0])}</div><div class="stat-value">${esc(x[1])}</div><div class="stat-note">${esc(x[2]||'')}</div></div>`).join('')}</div>`; }
function setTitle(page){ $('#pageTitle').textContent=titles[page]||'TEXT HACK'; document.querySelectorAll('.nav-item').forEach(n=>n.classList.toggle('active',n.dataset.page===page)); }
async function show(page){
  setTitle(page);
  try {
    if(page==='dashboard') return dashboard();
    if(page==='lab') return lab();
    if(page==='pipeline') return pipeline();
    if(page==='documents') return documents();
    if(page==='scheduler') return scheduler();
    if(page==='history') return history();
    if(page==='map') return map();
    if(page==='overview') return overview();
  } catch(e) { content.innerHTML=`<div class="card"><h2 class="error">Unable to load this view</h2><p class="muted">${esc(e.message)}</p></div>`; }
}
document.querySelectorAll('.nav-item').forEach(n=>n.addEventListener('click',()=>show(n.dataset.page)));

function dashboard(){
  content.innerHTML=`
  <div class="hero">
    <div class="pills"><span class="pill">JAVA ENGINE ONLINE</span><span class="pill">8 EXECUTABLE ALGORITHMS</span><span class="pill">HEFT V3 SCHEDULER</span></div>
    <h2>Turn text-processing problems into measurable DSA workflows.</h2>
    <p class="muted">TEXT HACK combines pattern matching, document comparison, sequence alignment, graph optimization and intelligent task scheduling in one execution platform. Select your inputs, run the algorithms, and inspect the real Java results.</p>
    <div class="form-actions"><button class="button primary" onclick="show('lab')">Open Algorithm Lab</button><button class="button secondary" onclick="show('pipeline')">Run Intelligent Pipeline</button><button class="button secondary" onclick="show('documents')">Manage Documents</button></div>
  </div>
  <div id="dashStats"></div>
  <div class="card quick-run">
    <div class="section-bar"><div><h2>Quick Execution</h2><p class="muted small">Run one algorithm immediately without leaving the dashboard.</p></div><span class="badge">LIVE JAVA RESULT</span></div>
    <div class="form-grid quick-grid"><div class="field"><label>Algorithm<select id="quickAlgorithm" onchange="quickFields()"><option value="kmp">KMP — Exact Pattern Search</option><option value="rabin-karp">Rabin-Karp — Pattern Search</option><option value="z">Z Algorithm — Pattern Analysis</option><option value="edit-distance">Edit Distance — Document Comparison</option><option value="suffix-lcp">Suffix Array + LCP — Document Similarity</option><option value="alignment">Sequence Alignment</option><option value="edmonds-karp">Edmonds-Karp — Maximum Flow</option><option value="miller-rabin">Miller-Rabin — Primality Test</option></select></label></div><div id="quickInputs" class="field"></div></div>
    <div class="form-actions"><button class="button primary" onclick="runQuick()">▶ Execute</button><button class="button ghost" onclick="show('lab')">Open Full Lab</button></div>
    <div id="quickResult"></div>
  </div>
  <div class="section-title">Platform capabilities</div>
  <div class="grid cards">
    <div class="card"><div class="feature-title">Real algorithm execution</div><p class="feature-copy">The browser sends your selected inputs to the Java backend. Results are generated by the project’s DSA implementations.</p></div>
    <div class="card"><div class="feature-title">Flexible document selection</div><p class="feature-copy">Use any corpus document or upload your own text document. Uploaded files become available in every relevant selector.</p></div>
    <div class="card"><div class="feature-title">Dependency-aware scheduling</div><p class="feature-copy">The six-stage pipeline respects task dependencies and uses Runtime-aware HEFT V3 to assign logical resources.</p></div>
  </div>`;
  quickFields(); loadDashboardStats();
}
async function loadDashboardStats(){
  try{const s=await api('/api/system');$('#dashStats').innerHTML=stats([['Documents',s.documents,'Corpus + uploaded files'],['Algorithms',s.algorithms,'Executable Java algorithms'],['Workers',s.workers,'Logical scheduler resources'],['Pipeline tasks',s.pipelineTasks,'Dependency-aware stages']]);}
  catch(e){$('#dashStats').innerHTML='';}
}
function quickFields(){
  const a=$('#quickAlgorithm')?.value, el=$('#quickInputs'); if(!el)return;
  if(['kmp','rabin-karp','z'].includes(a)) el.innerHTML=`<label>Document<select id="qdoc">${docOptions('D01')}</select><input id="qpattern" placeholder="Enter a pattern" value="data"></label>`;
  else if(['edit-distance','suffix-lcp','alignment'].includes(a)) el.innerHTML=`<label>Documents<div style="display:grid;grid-template-columns:1fr 1fr;gap:7px"><select id="qdoc">${docOptions('D01')}</select><select id="qdoc2">${docOptions('D05')}</select></div></label>`;
  else if(a==='miller-rabin') el.innerHTML=`<label>Number<input id="qnumber" type="number" min="2" value="104729"></label>`;
  else el.innerHTML=`<label>Input<select disabled><option>Built-in project flow network</option></select></label>`;
}
async function runQuick(){
  const a=$('#quickAlgorithm').value, out=$('#quickResult'); out.innerHTML=`<div class="result"><span class="spinner"></span> Executing ${label(a)} in Java…</div>`;
  let q=`algorithm=${encodeURIComponent(a)}`;
  if(['kmp','rabin-karp','z'].includes(a)) q+=`&document=${encodeURIComponent($('#qdoc').value)}&pattern=${encodeURIComponent($('#qpattern').value.trim())}`;
  else if(['edit-distance','suffix-lcp','alignment'].includes(a)) q+=`&document=${encodeURIComponent($('#qdoc').value)}&secondDocument=${encodeURIComponent($('#qdoc2').value)}`;
  else if(a==='miller-rabin') q+=`&number=${encodeURIComponent($('#qnumber').value)}`;
  try{renderResult(out,await api('/api/run?'+q));toast(`${label(a)} completed.`);}catch(e){out.innerHTML=`<div class="result error"><b>Execution failed</b><p>${esc(e.message)}</p></div>`;}
}
function renderResult(target,r){
  const el=typeof target==='string'?$(target):target;if(!el)return;
  const pairs=[];
  Object.entries(r).forEach(([k,v])=>{if(Array.isArray(v)||v&&typeof v==='object'||k==='actualRuntimeMs')return;pairs.push(`<div class="mini"><span>${esc(k.replace(/([A-Z])/g,' $1'))}</span><b>${esc(v)}</b></div>`);});
  el.innerHTML=`<div class="result"><div class="result-head"><div class="result-title">Execution Result</div><span class="badge">JAVA BACKEND</span></div><div class="kv">${pairs.slice(0,12).join('')}</div>${Array.isArray(r.matches)?`<p class="muted small">Match positions: <span class="mono">${esc(JSON.stringify(r.matches))}</span></p>`:''}${r.augmentingPaths!==undefined?`<p class="muted small">Augmenting paths used: <b>${esc(r.augmentingPaths)}</b></p>`:''}${r.actualRuntimeMs!==undefined?`<p class="muted small">Measured runtime: <b>${esc(r.actualRuntimeMs)} ms</b></p>`:''}</div>`;
}

function lab(){
  content.innerHTML=`
  <div class="hero"><div class="pills"><span class="pill">ONE WORKSPACE</span><span class="pill">8 ALGORITHMS</span><span class="pill">CUSTOM INPUTS</span></div><h2>Algorithm Lab</h2><p class="muted">Choose any executable algorithm from one workspace. The required inputs change automatically, so you can test documents, patterns, comparisons and numbers without opening separate algorithm pages.</p></div>
  <div class="card" style="margin-top:15px"><div class="section-bar"><div><h2>Algorithm Execution</h2><p id="labDescription" class="muted small"></p></div><span class="badge">REAL-TIME</span></div>
    <div class="field"><label>Algorithm<select id="labAlgorithm" onchange="renderLabInputs()"><option value="kmp">KMP — Exact Pattern Search</option><option value="rabin-karp">Rabin-Karp — Pattern Search</option><option value="z">Z Algorithm — Pattern Analysis</option><option value="suffix-lcp">Suffix Array + LCP — Document Similarity</option><option value="edit-distance">Edit Distance — Fuzzy Document Similarity</option><option value="alignment">Sequence Alignment — Cross-Document Alignment</option><option value="edmonds-karp">Edmonds-Karp — Maximum Flow</option><option value="miller-rabin">Miller-Rabin — Primality Testing</option></select></label></div>
    <div id="labInputs" style="margin-top:13px"></div>
    <div class="form-actions"><button class="button primary" onclick="runLab()">▶ Execute Selected Algorithm</button><button class="button ghost" onclick="$('#labResult').innerHTML=''">Clear Result</button></div>
    <div id="labResult"></div>
  </div>
  <div class="card" style="margin-top:15px"><div class="section-bar"><div><h2>Document Comparison</h2><p class="muted small">Choose any two available documents and select the comparison method.</p></div></div>
    <div class="form-grid">${docSelect('cmpA','Primary document','D01')}${docSelect('cmpB','Comparison document','D05')}<div class="field" style="grid-column:1/-1"><label>Comparison method<select id="cmpMethod"><option value="edit-distance">Edit Distance — Character-level similarity</option><option value="suffix-lcp">Suffix Array + LCP — Structural similarity</option><option value="alignment">Sequence Alignment — Alignment score</option></select></label></div></div>
    <div class="form-actions"><button class="button primary" onclick="runDocComparison()">▶ Compare Documents</button></div><div id="cmpResult"></div>
  </div>
  <div class="card" style="margin-top:15px"><div class="section-bar"><div><h2>Pattern Algorithm Comparison</h2><p class="muted small">Run KMP, Rabin-Karp and Z Algorithm on the same document and pattern.</p></div></div>
    <div class="form-grid">${docSelect('benchDoc','Document','D01')}<div class="field"><label>Pattern<input id="benchPattern" value="data" placeholder="Enter a pattern"></label></div></div>
    <div class="form-actions"><button class="button secondary" onclick="runBenchmark()">▶ Compare Search Algorithms</button></div><div id="benchmarkResult"></div>
  </div>`;
  renderLabInputs();
}
function renderLabInputs(){
  const a=$('#labAlgorithm').value, input=$('#labInputs'), desc=$('#labDescription');
  const descriptions={kmp:'Exact pattern matching using prefix-function preprocessing.', 'rabin-karp':'Pattern matching using rolling-hash comparison.', z:'Pattern occurrence detection using the Z structure.', 'suffix-lcp':'Builds a suffix array and LCP structure for the selected comparison document.', 'edit-distance':'Computes character-level edit distance and a normalized similarity estimate.', alignment:'Computes the sequence-alignment score for two documents.', 'edmonds-karp':'Runs the project’s configured flow network and returns its maximum flow.', 'miller-rabin':'Tests a user-supplied integer using the project Miller-Rabin implementation.'};
  desc.textContent=descriptions[a]||'';
  if(['kmp','rabin-karp','z'].includes(a)) input.innerHTML=`<div class="form-grid">${docSelect('labDoc','Document','D01')}<div class="field"><label>Pattern<input id="labPattern" value="data" placeholder="Enter a pattern"></label></div></div>`;
  else if(['suffix-lcp','edit-distance','alignment'].includes(a)) input.innerHTML=`<div class="form-grid">${docSelect('labDoc','Primary document','D01')}${docSelect('labDoc2','Comparison document','D05')}</div>`;
  else if(a==='miller-rabin') input.innerHTML=`<div class="form-grid"><div class="field"><label>Number<input id="labNumber" type="number" min="2" value="104729"></label></div></div>`;
  else input.innerHTML=`<div class="result"><b>Configured project graph</b><p class="muted small">No document input is required. The Java backend executes the configured Edmonds-Karp network.</p></div>`;
}
async function runLab(){
  const a=$('#labAlgorithm').value, out=$('#labResult');out.innerHTML=`<div class="result"><span class="spinner"></span> Executing ${label(a)} in Java…</div>`;
  let q=`algorithm=${encodeURIComponent(a)}`;
  if(['kmp','rabin-karp','z'].includes(a)){const p=$('#labPattern').value.trim();if(!p){out.innerHTML=`<div class="result error"><b>Pattern required</b><p>Enter a pattern before execution.</p></div>`;return}q+=`&document=${encodeURIComponent($('#labDoc').value)}&pattern=${encodeURIComponent(p)}`;}
  else if(['suffix-lcp','edit-distance','alignment'].includes(a))q+=`&document=${encodeURIComponent($('#labDoc').value)}&secondDocument=${encodeURIComponent($('#labDoc2').value)}`;
  else if(a==='miller-rabin')q+=`&number=${encodeURIComponent($('#labNumber').value)}`;
  try{renderResult(out,await api('/api/run?'+q));toast(`${label(a)} completed.`);}catch(e){out.innerHTML=`<div class="result error"><b>Execution failed</b><p>${esc(e.message)}</p></div>`;}
}
async function runDocComparison(){
  const out=$('#cmpResult');out.innerHTML='<div class="result"><span class="spinner"></span> Comparing the selected documents…</div>';
  try{renderResult(out,await api(`/api/run?algorithm=${$('#cmpMethod').value}&document=${encodeURIComponent($('#cmpA').value)}&secondDocument=${encodeURIComponent($('#cmpB').value)}`));toast('Document comparison completed.');}catch(e){out.innerHTML=`<div class="result error"><b>Comparison failed</b><p>${esc(e.message)}</p></div>`;}
}
async function runBenchmark(){
  const out=$('#benchmarkResult'),p=$('#benchPattern').value.trim();if(!p){out.innerHTML='<div class="result error"><b>Pattern required</b><p>Enter a pattern before running the comparison.</p></div>';return}
  out.innerHTML='<div class="result"><span class="spinner"></span> Running KMP, Rabin-Karp and Z Algorithm on identical input…</div>';
  try{const r=await api(`/api/compare?document=${encodeURIComponent($('#benchDoc').value)}&pattern=${encodeURIComponent(p)}`);out.innerHTML=`<div class="result"><div class="result-head"><div class="result-title">Search Algorithm Benchmark</div><span class="badge">JAVA BACKEND</span></div><p class="muted small">Document <b>${esc($('#benchDoc').value)}</b> · Pattern <b>"${esc(p)}"</b></p><div class="table-wrap"><table class="summary"><thead><tr><th>Algorithm</th><th>Matches</th><th>Measured runtime</th></tr></thead><tbody>${r.map(x=>`<tr><td><b>${esc(x.algorithm)}</b></td><td>${esc(x.matches)}</td><td>${esc(x.runtimeMs)} ms</td></tr>`).join('')}</tbody></table></div></div>`;toast('Search algorithms compared.');}catch(e){out.innerHTML=`<div class="result error"><b>Benchmark failed</b><p>${esc(e.message)}</p></div>`;}
}

function pipeline(){
  content.innerHTML=`<div class="hero"><div class="pills"><span class="pill">6 STAGES</span><span class="pill">DEPENDENCY DAG</span><span class="pill">RUNTIME-AWARE HEFT V3</span></div><h2>Intelligent Text Processing Pipeline</h2><p class="muted">Choose the primary document, comparison document and search pattern. TEXT HACK then executes the six-stage workflow, respects dependencies, assigns logical resources and reports the measured result.</p></div>
  <div class="card" style="margin-top:15px"><div class="section-bar"><div><h2>Pipeline Inputs</h2><p class="muted small">All three values are user-controlled and are sent directly to the Java backend.</p></div><span class="badge">LIVE INPUT</span></div>
    <div class="form-grid">${docSelect('pipePrimary','Primary document','D01')}${docSelect('pipeComparison','Comparison document','D05')}<div class="field" style="grid-column:1/-1"><label>Search pattern<input id="pipePattern" value="data" placeholder="Used by KMP, Rabin-Karp and Z Algorithm"></label></div></div>
    <div class="form-actions"><button class="button primary" id="runPipelineBtn" onclick="runPipeline()">▶ Run Intelligent Pipeline</button><span id="pipelineStatus" class="top-note"></span></div><div class="progress"><i id="pipelineProgress"></i></div>
  </div><div id="pipelineResult"></div>`;
}
async function runPipeline(){
  const btn=$('#runPipelineBtn'), progress=$('#pipelineProgress'), status=$('#pipelineStatus'), out=$('#pipelineResult'), pattern=$('#pipePattern').value.trim();
  if(!pattern){status.textContent='Enter a search pattern.';return}
  btn.disabled=true;btn.innerHTML='<span class="spinner"></span> Running pipeline';progress.style.width='35%';status.textContent='Validating inputs and executing dependency-aware tasks…';out.innerHTML='';
  try{const r=await api(`/api/pipeline?primary=${encodeURIComponent($('#pipePrimary').value)}&comparison=${encodeURIComponent($('#pipeComparison').value)}&pattern=${encodeURIComponent(pattern)}`);progress.style.width='100%';status.textContent='Pipeline completed successfully.';renderPipeline(r);toast('Intelligent pipeline completed.');}catch(e){progress.style.width='0';status.textContent=e.message;out.innerHTML=`<div class="result error"><b>Pipeline execution failed</b><p>${esc(e.message)}</p></div>`;}finally{btn.disabled=false;btn.textContent='▶ Run Intelligent Pipeline';}
}
function renderPipeline(p){
  const rows=(p.tasks||[]).map(t=>`<tr><td><b>${esc(t.stage)}</b></td><td>${esc(t.stageName)}</td><td><b>${esc(t.algorithm)}</b></td><td>${esc(t.cpu)}</td><td><span class="badge blue">${esc(t.resource)}</span></td><td>Round ${esc(t.round)}</td><td>${esc(t.result)}</td><td>${esc(t.runtimeMs)} ms</td></tr>`).join('');
  $('#pipelineResult').innerHTML=`<div class="card" style="margin-top:15px"><div class="section-bar"><div><h2>Pipeline Execution Report</h2><p class="muted small">${esc(p.primaryDocument)} → ${esc(p.comparisonDocument)} · Pattern "${esc(p.pattern)}"</p></div><span class="badge">${esc(p.scheduler)}</span></div><div class="table-wrap"><table class="summary"><thead><tr><th>Stage</th><th>Processing task</th><th>Algorithm</th><th>CPU</th><th>Resource</th><th>Round</th><th>Result</th><th>Runtime</th></tr></thead><tbody>${rows}</tbody></table></div>${stats([['Completed',`${p.completed}/${p.total}`,'Pipeline tasks'],['Failed',p.failed,'Execution errors'],['Blocked',p.blocked,'Dependency blocks'],['Rounds',p.rounds,'Dependency rounds'],['Makespan',`${p.makespan} ms`,'Measured wall time'],['Throughput',`${p.throughput} tasks/sec`,'Completed tasks / second']])}<div class="result"><b class="success">● ${esc(p.status)}</b><p class="muted small">The scheduler used dependency-aware Runtime-aware HEFT V3 to select logical resources for ready tasks.</p></div></div>`;
}

async function documents(){
  const d=await refreshDocs();
  content.innerHTML=`<div class="hero"><div class="pills"><span class="pill">CORPUS</span><span class="pill">UPLOAD</span><span class="pill">PREVIEW</span></div><h2>Document Library</h2><p class="muted">Browse the built-in corpus and upload your own text documents. Uploaded files are stored by the Java backend and immediately become available to the Algorithm Lab and Intelligent Pipeline.</p></div>
  <div class="card" style="margin-top:15px"><div class="section-bar"><div><h2>Upload Document</h2><p class="muted small">Accepted formats: .txt, .md and .csv.</p></div></div><div id="dropzone" class="dropzone"><strong>Drop a document here</strong><span>or click to choose a file</span><input id="fileInput" type="file" accept=".txt,.md,.csv,text/plain,text/markdown,text/csv" hidden></div><div class="form-actions"><button class="button primary" onclick="$('#fileInput').click()">Choose Document</button></div><div id="uploadResult"></div></div>
  <div class="card" style="margin-top:15px"><div class="section-bar"><div><h2>Available Documents</h2><p class="muted small">${d.length} documents are currently available for execution.</p></div><span class="badge">LIVE CORPUS</span></div><div class="table-wrap"><table class="summary"><thead><tr><th>ID</th><th>Document</th><th>Characters</th><th>Words</th><th>Lines</th><th>Source</th><th>Action</th></tr></thead><tbody>${d.map(x=>`<tr><td><b>${esc(x.id)}</b></td><td>${esc(x.name)}</td><td>${x.characters}</td><td>${x.words}</td><td>${x.lines}</td><td>${x.uploaded?'<span class="badge blue">Uploaded</span>':'<span class="badge">Corpus</span>'}</td><td><button class="button secondary" onclick="previewDocument('${esc(x.id)}')">Preview</button></td></tr>`).join('')}</tbody></table></div><div id="documentPreview"></div></div>`;
  setupUpload();
}
function setupUpload(){const zone=$('#dropzone'),input=$('#fileInput');zone.addEventListener('click',e=>{if(e.target!==input)input.click()});['dragenter','dragover'].forEach(ev=>zone.addEventListener(ev,e=>{e.preventDefault();zone.classList.add('drag')}));['dragleave','drop'].forEach(ev=>zone.addEventListener(ev,e=>{e.preventDefault();zone.classList.remove('drag')}));zone.addEventListener('drop',e=>{if(e.dataTransfer.files[0])uploadFile(e.dataTransfer.files[0])});input.addEventListener('change',e=>{if(e.target.files[0])uploadFile(e.target.files[0])});}
async function uploadFile(file){
  if(!/\.(txt|md|csv)$/i.test(file.name)){toast('Please upload a .txt, .md or .csv file.',true);return}
  $('#uploadResult').innerHTML=`<div class="result"><span class="spinner"></span> Uploading and indexing <b>${esc(file.name)}</b>…</div>`;
  const fd=new FormData();fd.append('file',file);
  try{const r=await api('/api/upload',{method:'POST',body:fd});toast('Document uploaded successfully.');await documents();$('#uploadResult').innerHTML=`<div class="result"><b class="success">Upload complete</b><p class="muted small">${esc(r.id)} · ${esc(r.name)} · ${r.characters} characters · ${r.words} words. The document is now available in all relevant selectors.</p></div>`;}catch(e){$('#uploadResult').innerHTML=`<div class="result error"><b>Upload failed</b><p>${esc(e.message)}</p></div>`;}
}
async function previewDocument(id){const r=await api('/api/document?id='+encodeURIComponent(id));$('#documentPreview').innerHTML=`<div class="result"><div class="result-head"><div class="result-title">${esc(r.id)} · ${esc(r.name)}</div><button class="button secondary" onclick="$('#documentPreview').innerHTML=''">Close</button></div><div class="code-block">${esc(r.content)}</div></div>`;}

async function scheduler(){
  const [r,d]=await Promise.all([api('/api/resources'),api('/api/corpus')]);
  content.innerHTML=`<div class="hero"><div class="pills"><span class="pill">RUNTIME-AWARE HEFT V3</span><span class="pill">3 LOGICAL WORKERS</span></div><h2>Scheduler & Resources</h2><p class="muted">The scheduler treats ready algorithms as tasks, respects dependencies, checks CPU/RAM requirements and selects a logical worker using predicted finish time. The workers are logical resources, not separate physical computers.</p></div>
  <div class="grid cards">${r.map(x=>`<div class="card"><div class="section-bar"><div><div class="feature-title">${esc(x.id)} · ${esc(x.name)}</div><div class="worker-meta">Logical scheduler resource</div></div><span class="badge">${esc(x.status)}</span></div><div class="worker"><div><div class="worker-name">CPU capacity</div></div><b>${x.cpu} cores</b></div><div class="worker"><div><div class="worker-name">Memory capacity</div></div><b>${x.ram} MB</b></div></div>`).join('')}</div>
  <div class="card" style="margin-top:15px"><div class="section-bar"><div><h2>Workload Snapshot</h2><p class="muted small">Current corpus metadata used by the web layer for workload visibility.</p></div></div><div class="table-wrap"><table class="summary"><thead><tr><th>Document</th><th>Characters</th><th>Words</th><th>Lines</th><th>Estimated CPU</th><th>Estimated RAM</th></tr></thead><tbody>${d.map(x=>`<tr><td><b>${esc(x.id)}</b> · ${esc(x.name)}</td><td>${x.characters}</td><td>${x.words}</td><td>${x.lines}</td><td>${x.cpu} core(s)</td><td>${x.ram} MB</td></tr>`).join('')}</tbody></table></div></div>`;
}
async function history(){
  const h=await api('/api/history');
  content.innerHTML=`<div class="hero"><div class="pills"><span class="pill">SESSION HISTORY</span><span class="pill">MEASURED EXECUTIONS</span></div><h2>Results & History</h2><p class="muted">Recent algorithm requests from this Java server session. Each entry is generated by an actual API execution.</p></div><div class="card" style="margin-top:15px"><div class="section-bar"><div><h2>Recent Executions</h2><p class="muted small">${h.requests} recorded request(s) in this session.</p></div></div>${h.recent.length?`<div class="table-wrap"><table class="summary"><thead><tr><th>Time</th><th>Algorithm</th><th>Documents</th><th>Result</th></tr></thead><tbody>${h.recent.map(x=>`<tr><td>${esc(x.time)}</td><td><b>${esc(x.algorithm)}</b></td><td>${esc(x.documents)}</td><td>${esc(x.result)}</td></tr>`).join('')}</tbody></table></div>`:'<div class="empty">No executions yet. Run an algorithm or the intelligent pipeline to create history.</div>'}</div>`;
}
function map(){
  const a=[['KMP','Exact Pattern Search','O(n + m)'],['Rabin-Karp','Keyword Verification','Average O(n + m)'],['Z Algorithm','Pattern Structure Analysis','O(n + m)'],['Suffix Array + LCP','Text Index Construction','Index + LCP'],['Edit Distance','Fuzzy Document Similarity','O(mn)'],['Sequence Alignment','Cross-Document Alignment','O(mn)'],['Edmonds-Karp','Maximum Flow','O(VE²)'],['Miller-Rabin','Large-prime Testing','Probabilistic']];
  content.innerHTML=`<div class="hero"><div class="pills"><span class="pill">DSA-3</span><span class="pill">PROBLEM → ALGORITHM → COST</span></div><h2>DSA-3 Algorithm Map</h2><p class="muted">A concise view of how the project maps text-processing and optimization problems to the implemented algorithms.</p></div><div class="algo-grid" style="margin-top:15px">${a.map(x=>`<div class="card algo-card"><div class="algo-name">${esc(x[0])}</div><div class="algo-group">${esc(x[1])}</div><div class="algo-cost">Complexity / role: <b>${esc(x[2])}</b></div></div>`).join('')}</div>`;
}
function overview(){
  content.innerHTML=`<div class="hero"><div class="pills"><span class="pill">TEXT HACK ARCHITECTURE</span><span class="pill">REAL JAVA EXECUTION</span></div><h2>System Architecture</h2><p class="muted">TEXT HACK connects user-controlled inputs to the existing DSA implementations and, for the intelligent pipeline, adds dependency-aware Runtime-aware HEFT V3 scheduling.</p></div>
  <div class="architecture"><div class="arch-step"><strong>User Input</strong><span>Documents, patterns, numbers and comparisons</span></div><div class="arch-arrow">→</div><div class="arch-step"><strong>Web Interface</strong><span>Single workspace for execution and results</span></div><div class="arch-arrow">→</div><div class="arch-step"><strong>Java Backend</strong><span>HTTP API and input validation</span></div><div class="arch-arrow">→</div><div class="arch-step"><strong>DSA Engine</strong><span>Existing Java algorithm implementations</span></div><div class="arch-arrow">→</div><div class="arch-step"><strong>Result</strong><span>Output, runtime, resource and history</span></div></div>
  <div class="grid cards"><div class="card"><div class="feature-title">Pipeline scheduling</div><p class="feature-copy">Ready tasks are ranked, dependency constraints are respected, CPU/RAM requirements are checked and a logical resource is selected.</p></div><div class="card"><div class="feature-title">Runtime measurement</div><p class="feature-copy">Execution time is measured by the Java backend and displayed with the algorithm result.</p></div><div class="card"><div class="feature-title">Document management</div><p class="feature-copy">Built-in documents and uploaded text files are stored and exposed through the same API.</p></div></div>
  <div class="card" style="margin-top:15px"><h2>Execution Flow</h2><div class="code-block">USER INPUT → FRONTEND → JAVA API → EXISTING DSA ALGORITHM → RESULT\n                              ↘ INTELLIGENT PIPELINE → HEFT V3 → LOGICAL RESOURCE → MEASURED RESULT</div></div>`;
}

async function boot(){
  try{await refreshDocs();$('#serverState').textContent='Backend online';$('#apiText').textContent='API ready';}
  catch(e){$('#serverState').textContent='Backend unavailable';$('#apiText').textContent='API unavailable';toast('Java backend is not reachable on localhost:8000.',true);}
  show('dashboard');
}
boot();
