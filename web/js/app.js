// Codely Enterprise Web Application
const API_BASE = '';

// App State
let state = {
  theme: localStorage.getItem('ct_theme') || 'dark',
  languages: [],
  problems: [],
  currentProblem: null,
  currentCourse: null,
  currentChapter: null,
  assessmentExam: null,
  assessmentIndex: 0,
  assessmentAnswers: {}, // problemId -> { passed, code, language }
  examSecondsLeft: 0,
  examTimerInterval: null,
  solvedProblemIds: new Set(),
  playgroundLang: 'java',
  workbenchLang: 'java',
  // MCQ state
  mcqQuestions: [],
  mcqIndex: 0,
  mcqScore: 0,
  mcqLang: 'all',
  mcqDifficulty: 'all',
  mcqAnswered: false,
  // Auth state
  user: null,
  token: localStorage.getItem('codely_token') || null
};

// Editors
let playgroundEditor = null;
let workbenchEditor = null;
let isMonacoReady = false;

// Initialize App
document.addEventListener('DOMContentLoaded', async () => {
  initTheme();
  setupNavigation();
  setupPlaygroundEvents();
  setupWorkbenchEvents();
  setupMcqEvents();
  setupAuthEvents();
  setupModalEvents();

  await initAuth();
  await loadLanguages();
  await loadDashboard();
  await loadProblems();
  await loadCourses();
  await loadAssessments();

  initMonaco();
});

// ==================== THEME MANAGEMENT ====================
function initTheme() {
  document.documentElement.setAttribute('data-theme', state.theme);
  updateThemeIcon();

  const themeBtn = document.getElementById('theme-toggle-btn');
  themeBtn.addEventListener('click', () => {
    state.theme = state.theme === 'dark' ? 'light' : 'dark';
    document.documentElement.setAttribute('data-theme', state.theme);
    localStorage.setItem('ct_theme', state.theme);
    updateThemeIcon();
    if (isMonacoReady && window.monaco) {
      monaco.editor.setTheme(state.theme === 'dark' ? 'vs-dark' : 'vs');
    }
  });
}

function updateThemeIcon() {
  const icon = document.getElementById('theme-icon');
  icon.textContent = state.theme === 'dark' ? '🌙' : '☀️';
}

// ==================== MONACO EDITOR ====================
function initMonaco() {
  let monacoWatchdog = setTimeout(() => {
    if (!isMonacoReady) {
      console.warn("Monaco Editor CDN load timeout (2.5s). Activating native fallback code editor.");
      fallbackToTextareas();
    }
  }, 2500);

  if (typeof require !== 'undefined' && require.config) {
    try {
      require.config({ paths: { vs: 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.45.0/min/vs' } });
      require(['vs/editor/editor.main'], function () {
        clearTimeout(monacoWatchdog);
        isMonacoReady = true;

        // Hide fallback textareas
        const pgArea = document.getElementById('playground-fallback-code');
        const wbArea = document.getElementById('workbench-fallback-code');
        if (pgArea) pgArea.style.display = 'none';
        if (wbArea) wbArea.style.display = 'none';

        const pgContainer = document.getElementById('playground-editor');
        const wbContainer = document.getElementById('workbench-editor');
        if (pgContainer) pgContainer.style.display = 'block';
        if (wbContainer) wbContainer.style.display = 'block';

        // Playground Editor
        playgroundEditor = monaco.editor.create(pgContainer, {
          value: getLanguageTemplate(state.playgroundLang),
          language: getMonacoLang(state.playgroundLang),
          theme: state.theme === 'dark' ? 'vs-dark' : 'vs',
          fontSize: 14,
          fontFamily: "'Fira Code', 'Cascadia Code', 'Consolas', monospace",
          automaticLayout: true,
          minimap: { enabled: false },
          lineNumbers: 'on',
          scrollBeyondLastLine: false,
          padding: { top: 12, bottom: 12 }
        });

        // Workbench Editor
        workbenchEditor = monaco.editor.create(wbContainer, {
          value: "// Select a problem from the catalog...",
          language: getMonacoLang(state.workbenchLang),
          theme: state.theme === 'dark' ? 'vs-dark' : 'vs',
          fontSize: 14,
          fontFamily: "'Fira Code', 'Cascadia Code', 'Consolas', monospace",
          automaticLayout: true,
          minimap: { enabled: false },
          lineNumbers: 'on',
          scrollBeyondLastLine: false,
          padding: { top: 12, bottom: 12 }
        });

        // Sync values to textareas on change
        playgroundEditor.onDidChangeModelContent(() => {
          if (pgArea) pgArea.value = playgroundEditor.getValue();
        });
        workbenchEditor.onDidChangeModelContent(() => {
          if (wbArea) wbArea.value = workbenchEditor.getValue();
        });

        // Shortcut: Ctrl + Enter to run code
        window.addEventListener('keydown', (e) => {
          if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
            const activeView = document.querySelector('.view-section.active');
            if (activeView && activeView.id === 'view-playground') {
              document.getElementById('playground-run-btn').click();
            } else if (activeView && activeView.id === 'view-problems') {
              document.getElementById('workbench-run-btn').click();
            }
          }
        });
      }, function (err) {
        clearTimeout(monacoWatchdog);
        console.warn("Failed to load Monaco from CDN:", err);
        fallbackToTextareas();
      });
    } catch (e) {
      clearTimeout(monacoWatchdog);
      fallbackToTextareas();
    }
  } else {
    clearTimeout(monacoWatchdog);
    fallbackToTextareas();
  }
}

function setupTextareaEditor(textarea) {
  if (!textarea || textarea.dataset.tabListenerAttached === 'true') return;
  textarea.dataset.tabListenerAttached = 'true';

  // Support Tab key for indentation
  textarea.addEventListener('keydown', (e) => {
    if (e.key === 'Tab') {
      e.preventDefault();
      const start = textarea.selectionStart;
      const end = textarea.selectionEnd;
      const val = textarea.value;
      textarea.value = val.substring(0, start) + '    ' + val.substring(end);
      textarea.selectionStart = textarea.selectionEnd = start + 4;
    }
  });
}

function fallbackToTextareas() {
  isMonacoReady = false;
  const pgContainer = document.getElementById('playground-editor');
  const wbContainer = document.getElementById('workbench-editor');
  if (pgContainer) pgContainer.style.display = 'none';
  if (wbContainer) wbContainer.style.display = 'none';

  const pgArea = document.getElementById('playground-fallback-code');
  const wbArea = document.getElementById('workbench-fallback-code');
  if (pgArea) {
    pgArea.style.display = 'block';
    setupTextareaEditor(pgArea);
    if (!pgArea.value) pgArea.value = getLanguageTemplate(state.playgroundLang);
  }
  if (wbArea) {
    wbArea.style.display = 'block';
    setupTextareaEditor(wbArea);
  }
}

function getEditorCode(isWorkbench = false) {
  if (isMonacoReady && window.monaco) {
    const editor = isWorkbench ? workbenchEditor : playgroundEditor;
    if (editor) return editor.getValue();
  }
  const textarea = isWorkbench ? document.getElementById('workbench-fallback-code') : document.getElementById('playground-fallback-code');
  return textarea ? textarea.value : '';
}

function setEditorCode(code, lang, isWorkbench = false) {
  if (isMonacoReady && window.monaco) {
    const editor = isWorkbench ? workbenchEditor : playgroundEditor;
    if (editor) {
      editor.setValue(code || '');
      monaco.editor.setModelLanguage(editor.getModel(), getMonacoLang(lang));
    }
  }
  // Keep fallback textarea synchronized at all times
  const textarea = isWorkbench ? document.getElementById('workbench-fallback-code') : document.getElementById('playground-fallback-code');
  if (textarea) {
    textarea.value = code || '';
    setupTextareaEditor(textarea);
  }
}

function getMonacoLang(lang) {
  switch (lang) {
    case 'java': return 'java';
    case 'python': case 'py': return 'python';
    case 'javascript': case 'js': return 'javascript';
    case 'c': return 'c';
    case 'cpp': case 'c++': return 'cpp';
    case 'sql': return 'sql';
    default: return 'plaintext';
  }
}

// ==================== NAVIGATION ====================
function setupNavigation() {
  const tabs = [
    { btn: 'tab-playground', view: 'view-playground' },
    { btn: 'tab-problems', view: 'view-problems' },
    { btn: 'tab-mcq', view: 'view-mcq' },
    { btn: 'tab-courses', view: 'view-courses' },
    { btn: 'tab-assessments', view: 'view-assessments' },
    { btn: 'tab-dashboard', view: 'view-dashboard' }
  ];

  tabs.forEach(item => {
    document.getElementById(item.btn).addEventListener('click', () => {
      switchView(item.btn, item.view);
    });
  });

  document.getElementById('brand-home-btn').addEventListener('click', () => {
    switchView('tab-playground', 'view-playground');
  });

  // Drawer tabs switching (Playground & Workbench)
  document.querySelectorAll('.drawer-tab').forEach(tab => {
    tab.addEventListener('click', (e) => {
      const parent = tab.closest('.bottom-drawer');
      parent.querySelectorAll('.drawer-tab').forEach(t => t.classList.remove('active'));
      parent.querySelectorAll('.drawer-pane').forEach(p => p.classList.remove('active'));

      tab.classList.add('active');
      const targetId = tab.getAttribute('data-target');
      const pane = parent.querySelector('#' + targetId);
      if (pane) pane.classList.add('active');
    });
  });
}

function switchView(tabBtnId, viewId) {
  document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));
  document.querySelectorAll('.view-section').forEach(v => v.classList.remove('active'));

  const btn = document.getElementById(tabBtnId);
  const view = document.getElementById(viewId);
  if (btn) btn.classList.add('active');
  if (view) view.classList.add('active');

  // Trigger Monaco layout update
  if (isMonacoReady && window.monaco) {
    setTimeout(() => {
      if (playgroundEditor) playgroundEditor.layout();
      if (workbenchEditor) workbenchEditor.layout();
    }, 50);
  }

  if (viewId === 'view-dashboard') {
    loadDashboard();
  }
}

// ==================== LANGUAGES & PLAYGROUND ====================
async function loadLanguages() {
  try {
    const res = await fetch('/api/languages');
    if (res.ok) {
      state.languages = await res.json();
    }
  } catch (err) {
    console.warn('Using default languages:', err);
  }
}

function getLanguageTemplate(langId) {
  const found = state.languages.find(l => l.id === langId);
  if (found) return found.template;
  if (langId === 'java') return "public class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, Codely!\");\n    }\n}";
  if (langId === 'python') return "print('Hello, Codely!')\n";
  if (langId === 'c') return "#include <stdio.h>\n\nint main() {\n    printf(\"Hello, Codely!\\n\");\n    return 0;\n}";
  if (langId === 'cpp') return "#include <iostream>\nusing namespace std;\n\nint main() {\n    cout << \"Hello, Codely!\" << endl;\n    return 0;\n}";
  if (langId === 'javascript') return "console.log('Hello, Codely!');";
  if (langId === 'sql') return "CREATE TABLE sample (id INT, msg TEXT);\nINSERT INTO sample VALUES (1, 'Hello Codely');\nSELECT * FROM sample;";
  return "";
}

function setupPlaygroundEvents() {
  const langSelect = document.getElementById('playground-lang-select');
  langSelect.addEventListener('change', (e) => {
    state.playgroundLang = e.target.value;
    setEditorCode(getLanguageTemplate(state.playgroundLang), state.playgroundLang, false);
  });

  document.getElementById('playground-reset-btn').addEventListener('click', () => {
    setEditorCode(getLanguageTemplate(state.playgroundLang), state.playgroundLang, false);
  });

  document.getElementById('playground-download-btn').addEventListener('click', () => {
    const code = getEditorCode(false);
    const extMap = { java: 'java', python: 'py', javascript: 'js', c: 'c', cpp: 'cpp', sql: 'sql' };
    const ext = extMap[state.playgroundLang] || 'txt';
    const blob = new Blob([code], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `solution.${ext}`;
    a.click();
    URL.revokeObjectURL(url);
  });

  const runBtn = document.getElementById('playground-run-btn');
  runBtn.addEventListener('click', async () => {
    const code = getEditorCode(false);
    const stdin = document.getElementById('playground-stdin').value;
    const outputBox = document.getElementById('pg-terminal-output');
    const statusTag = document.getElementById('pg-status-tag');
    const timeTag = document.getElementById('pg-time-tag');

    // Make output tab active
    document.querySelector('[data-target="pg-tab-output"]').click();

    runBtn.disabled = true;
    runBtn.innerHTML = '<span class="spinner"></span> Running...';
    statusTag.textContent = 'Compiling & Executing...';
    statusTag.style.color = 'var(--accent-cyan)';
    outputBox.textContent = 'Executing code in isolated environment...';

    try {
      const res = await fetch('/api/execute', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          language: state.playgroundLang,
          code: code,
          stdin: stdin
        })
      });

      const data = await res.json();
      timeTag.textContent = `${data.executionTimeMs || 0} ms`;

      if (data.timedOut) {
        statusTag.textContent = 'Time Limit Exceeded';
        statusTag.style.color = 'var(--accent-rose)';
        outputBox.innerHTML = `<span style="color: var(--accent-rose);">${escapeHtml(data.stderr || data.error)}</span>`;
      } else if (!data.success) {
        statusTag.textContent = data.exitCode !== 0 ? `Exit Code: ${data.exitCode}` : 'Error';
        statusTag.style.color = 'var(--accent-rose)';
        outputBox.innerHTML = `<span style="color: var(--accent-rose);">${escapeHtml(data.stderr || data.error)}</span>\n${escapeHtml(data.stdout)}`;
      } else {
        statusTag.textContent = 'Executed Successfully (Exit 0)';
        statusTag.style.color = 'var(--accent-green)';
        outputBox.textContent = data.stdout || '[Program executed with no standard output]';
      }
    } catch (err) {
      statusTag.textContent = 'Network / Server Error';
      statusTag.style.color = 'var(--accent-rose)';
      outputBox.textContent = 'Error contacting execution server: ' + err.message;
    } finally {
      runBtn.disabled = false;
      runBtn.innerHTML = '<span>⚡</span> Run Code';
    }
  });
}

// ==================== PRACTICE PROBLEMS ====================
async function loadProblems() {
  try {
    const res = await fetch('/api/problems');
    if (res.ok) {
      state.problems = await res.json();
      renderProblemsCatalog();
    }
  } catch (err) {
    console.error('Error loading problems:', err);
  }
}

function renderProblemsCatalog() {
  const tbody = document.getElementById('problems-table-body');
  tbody.innerHTML = '';

  const search = document.getElementById('problem-search').value.toLowerCase();
  const diffFilter = document.getElementById('difficulty-filter').value;

  const filtered = state.problems.filter(p => {
    const matchesSearch = p.title.toLowerCase().includes(search) || p.category.toLowerCase().includes(search);
    const matchesDiff = diffFilter === 'all' || p.difficulty === diffFilter;
    return matchesSearch && matchesDiff;
  });

  if (filtered.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" style="text-align: center; color: var(--text-muted); padding: 2rem;">No matching problems found.</td></tr>`;
    return;
  }

  filtered.forEach(p => {
    const tr = document.createElement('tr');
    const isSolved = state.solvedProblemIds.has(p.id);

    tr.innerHTML = `
      <td style="text-align: center;">
        <span style="font-size: 1.1rem; color: ${isSolved ? 'var(--accent-green)' : 'var(--text-muted)'};">
          ${isSolved ? '✓' : '○'}
        </span>
      </td>
      <td>
        <a class="problem-link" data-id="${p.id}">
          ${escapeHtml(p.title)}
        </a>
      </td>
      <td><span class="badge badge-tag">${escapeHtml(p.category)}</span></td>
      <td>
        <span class="badge ${p.difficulty === 'Easy' ? 'badge-easy' : (p.difficulty === 'Medium' ? 'badge-medium' : 'badge-hard')}">
          ${p.difficulty}
        </span>
      </td>
      <td>
        <button class="btn btn-secondary solve-btn" data-id="${p.id}" style="padding: 0.35rem 0.8rem; font-size: 0.8rem;">
          ${isSolved ? 'Solve Again' : 'Solve Challenge'}
        </button>
      </td>
    `;
    tbody.appendChild(tr);
  });

  // Attach click events
  tbody.querySelectorAll('.problem-link, .solve-btn').forEach(el => {
    el.addEventListener('click', (e) => {
      const id = el.getAttribute('data-id');
      openProblem(id);
    });
  });
}

function openProblem(problemId) {
  const p = state.problems.find(item => item.id === problemId);
  if (!p) return;

  state.currentProblem = p;

  document.getElementById('problems-catalog-container').style.display = 'none';
  document.getElementById('problem-workbench-container').style.display = 'flex';

  // Populate workbench metadata
  document.getElementById('workbench-title').textContent = p.title;
  document.getElementById('workbench-category').textContent = p.category;

  const diffBadge = document.getElementById('workbench-difficulty-badge');
  diffBadge.innerHTML = `<span class="badge ${p.difficulty === 'Easy' ? 'badge-easy' : (p.difficulty === 'Medium' ? 'badge-medium' : 'badge-hard')}">${p.difficulty}</span>`;

  document.getElementById('workbench-description').innerHTML = formatMarkdown(p.description);
  document.getElementById('workbench-input-spec').innerHTML = formatMarkdown(p.inputFormat);
  document.getElementById('workbench-output-spec').innerHTML = formatMarkdown(p.outputFormat);
  document.getElementById('workbench-constraints').textContent = p.constraints;
  document.getElementById('workbench-sample-input').textContent = p.sampleInput || '(No input required)';
  document.getElementById('workbench-sample-output').textContent = p.sampleOutput;
  document.getElementById('workbench-stdin').value = p.sampleInput || '';

  // Setup starter template for current language
  loadProblemTemplate();

  // Reset results pane
  document.getElementById('wb-results-summary').style.display = 'none';
  document.getElementById('wb-test-cases-list').innerHTML = `
    <div style="color: var(--text-muted); font-size: 0.88rem; padding: 1rem 0;">
      Click <strong>Run Code</strong> to test sample input, or <strong>Submit Solution</strong> to grade against all test cases.
    </div>
  `;

  // Editor layout and focus handling
  if (isMonacoReady && workbenchEditor) {
    setTimeout(() => {
      workbenchEditor.layout();
      workbenchEditor.focus();
    }, 50);
    setTimeout(() => {
      workbenchEditor.layout();
    }, 200);
  } else {
    const wbArea = document.getElementById('workbench-fallback-code');
    if (wbArea) {
      setupTextareaEditor(wbArea);
      setTimeout(() => wbArea.focus(), 50);
    }
  }
}

function loadProblemTemplate() {
  if (!state.currentProblem) return;
  const p = state.currentProblem;
  const lang = state.workbenchLang;
  let code = (p.starterTemplates && p.starterTemplates[lang]) || getLanguageTemplate(lang);
  setEditorCode(code, lang, true);
}

function setupWorkbenchEvents() {
  document.getElementById('back-to-catalog-btn').addEventListener('click', () => {
    document.getElementById('problem-workbench-container').style.display = 'none';
    document.getElementById('problems-catalog-container').style.display = 'block';
    renderProblemsCatalog();
  });

  const langSelect = document.getElementById('workbench-lang-select');
  langSelect.addEventListener('change', (e) => {
    state.workbenchLang = e.target.value;
    loadProblemTemplate();
  });

  document.getElementById('workbench-reset-btn').addEventListener('click', () => {
    loadProblemTemplate();
  });

  document.getElementById('copy-sample-input-btn').addEventListener('click', () => {
    if (state.currentProblem) {
      navigator.clipboard.writeText(state.currentProblem.sampleInput || '');
      const btn = document.getElementById('copy-sample-input-btn');
      btn.textContent = 'Copied!';
      setTimeout(() => btn.textContent = 'Copy', 1500);
    }
  });

  // Problem search & filters
  document.getElementById('problem-search').addEventListener('input', renderProblemsCatalog);
  document.getElementById('difficulty-filter').addEventListener('change', renderProblemsCatalog);

  // Run Code in Workbench
  const runBtn = document.getElementById('workbench-run-btn');
  runBtn.addEventListener('click', async () => {
    const code = getEditorCode(true);
    const stdin = document.getElementById('workbench-stdin').value;
    const outputBox = document.getElementById('wb-console-output');
    const statusTag = document.getElementById('wb-console-status');
    const timeTag = document.getElementById('wb-console-time');

    // Switch to Console tab
    document.querySelector('[data-target="wb-tab-console"]').click();

    runBtn.disabled = true;
    runBtn.innerHTML = '<span class="spinner"></span> Running...';
    statusTag.textContent = 'Running custom execution...';
    outputBox.textContent = 'Executing...';

    try {
      const res = await fetch('/api/execute', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          language: state.workbenchLang,
          code: code,
          stdin: stdin
        })
      });

      const data = await res.json();
      timeTag.textContent = `${data.executionTimeMs || 0} ms`;

      if (data.timedOut) {
        statusTag.textContent = 'Time Limit Exceeded';
        statusTag.style.color = 'var(--accent-rose)';
        outputBox.innerHTML = `<span style="color: var(--accent-rose);">${escapeHtml(data.stderr || data.error)}</span>`;
      } else if (!data.success) {
        statusTag.textContent = 'Execution Error';
        statusTag.style.color = 'var(--accent-rose)';
        outputBox.innerHTML = `<span style="color: var(--accent-rose);">${escapeHtml(data.stderr || data.error)}</span>\n${escapeHtml(data.stdout)}`;
      } else {
        statusTag.textContent = 'Success';
        statusTag.style.color = 'var(--accent-green)';
        outputBox.textContent = data.stdout || '[No standard output]';
      }
    } catch (err) {
      statusTag.textContent = 'Execution Failed';
      statusTag.style.color = 'var(--accent-rose)';
      outputBox.textContent = err.message;
    } finally {
      runBtn.disabled = false;
      runBtn.innerHTML = '<span>⚡</span> Run Code';
    }
  });

  // Submit Solution in Workbench
  const submitBtn = document.getElementById('workbench-submit-btn');
  submitBtn.addEventListener('click', async () => {
    if (!state.currentProblem) return;

    const code = getEditorCode(true);
    const resultsSummary = document.getElementById('wb-results-summary');
    const verdictTitle = document.getElementById('wb-verdict-title');
    const timeSummary = document.getElementById('wb-time-summary');
    const testCasesList = document.getElementById('wb-test-cases-list');

    // Switch to Test Cases tab
    document.querySelector('[data-target="wb-tab-testcases"]').click();

    submitBtn.disabled = true;
    submitBtn.innerHTML = '<span class="spinner"></span> Evaluating...';
    testCasesList.innerHTML = `
      <div style="color: var(--accent-cyan); font-size: 0.9rem; padding: 1.5rem; text-align: center;">
        <span class="spinner" style="border-top-color: var(--accent-cyan); width: 20px; height: 20px; margin-bottom: 0.5rem; display: block; margin: 0 auto 0.5rem auto;"></span>
        Grading submission against public and hidden test cases...
      </div>
    `;

    try {
      const res = await fetch('/api/submit', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          problemId: state.currentProblem.id,
          language: state.workbenchLang,
          code: code
        })
      });

      const report = await res.json();

      resultsSummary.style.display = 'flex';
      timeSummary.textContent = `Total Execution Time: ${report.totalTimeMs || 0} ms`;

      if (report.allPassed) {
        verdictTitle.className = 'results-verdict passed';
        verdictTitle.innerHTML = `🎉 Accepted (${report.passedTestCases}/${report.totalTestCases} Passed) +50 XP`;
        state.solvedProblemIds.add(state.currentProblem.id);
      } else {
        verdictTitle.className = 'results-verdict failed';
        verdictTitle.innerHTML = `❌ ${escapeHtml(report.verdict)} (${report.passedTestCases}/${report.totalTestCases} Passed)`;
      }

      // Render Test Case Cards
      testCasesList.innerHTML = '';
      if (report.testResults && report.testResults.length > 0) {
        report.testResults.forEach(tr => {
          const card = document.createElement('div');
          card.className = `test-case-card ${tr.passed ? 'passed' : 'failed'}`;
          card.innerHTML = `
            <div class="tc-header">
              <span>Test Case #${tr.testCaseNumber} ${tr.isHidden ? '<span class="badge badge-tag" style="font-size: 0.65rem;">Hidden</span>' : '<span class="badge badge-tag" style="font-size: 0.65rem;">Public</span>'}</span>
              <span style="color: ${tr.passed ? 'var(--accent-green)' : 'var(--accent-rose)'}; font-weight: 700;">
                ${tr.passed ? 'Passed ✓' : 'Failed ✗'} (${tr.executionTimeMs}ms)
              </span>
            </div>
            ${tr.input ? `<div class="tc-field"><div class="tc-field-label">Input:</div><div class="tc-field-val">${escapeHtml(tr.input)}</div></div>` : ''}
            <div class="tc-field">
              <div class="tc-field-label">Expected Output:</div>
              <div class="tc-field-val">${escapeHtml(tr.expectedOutput)}</div>
            </div>
            <div class="tc-field">
              <div class="tc-field-label">Actual Output:</div>
              <div class="tc-field-val" style="color: ${tr.passed ? 'var(--text-primary)' : 'var(--accent-rose)'};">${escapeHtml(tr.actualOutput || (tr.error ? tr.error : '[No output]'))}</div>
            </div>
          `;
          testCasesList.appendChild(card);
        });
      }

      // Update dashboard numbers
      await loadDashboard();
    } catch (err) {
      testCasesList.innerHTML = `<div style="color: var(--accent-rose);">Error submitting code: ${err.message}</div>`;
    } finally {
      submitBtn.disabled = false;
      submitBtn.innerHTML = '<span>🚀</span> Submit Solution';
    }
  });
}

// ==================== MCQ QUIZ MODULE ====================
function setupMcqEvents() {
  // Language pills
  const pillsContainer = document.getElementById('mcq-lang-pills');
  if (pillsContainer) {
    pillsContainer.querySelectorAll('.lang-pill-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        pillsContainer.querySelectorAll('.lang-pill-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        state.mcqLang = btn.getAttribute('data-lang');
        loadMcqQuestions(state.mcqLang, state.mcqDifficulty);
      });
    });
  }

  // Difficulty pills
  const diffPillsContainer = document.getElementById('mcq-diff-pills');
  if (diffPillsContainer) {
    diffPillsContainer.querySelectorAll('.diff-pill-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        diffPillsContainer.querySelectorAll('.diff-pill-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        state.mcqDifficulty = btn.getAttribute('data-diff');
        loadMcqQuestions(state.mcqLang, state.mcqDifficulty);
      });
    });
  }

  // Refresh button: fetches a fresh set of randomized questions
  const refreshBtn = document.getElementById('mcq-refresh-btn');
  if (refreshBtn) {
    refreshBtn.addEventListener('click', () => {
      loadMcqQuestions(state.mcqLang, state.mcqDifficulty);
    });
  }

  // Next question button
  const nextBtn = document.getElementById('mcq-next-btn');
  if (nextBtn) {
    nextBtn.addEventListener('click', () => {
      state.mcqIndex++;
      if (state.mcqIndex < state.mcqQuestions.length) {
        renderMcqQuestion();
      } else {
        finishMcqQuiz();
      }
    });
  }

  // Restart quiz button
  const restartBtn = document.getElementById('mcq-restart-btn');
  if (restartBtn) {
    restartBtn.addEventListener('click', () => {
      loadMcqQuestions(state.mcqLang, state.mcqDifficulty);
    });
  }

  // Go to lab button
  const gotoLabBtn = document.getElementById('mcq-goto-lab-btn');
  if (gotoLabBtn) {
    gotoLabBtn.addEventListener('click', () => {
      switchView('tab-problems', 'view-problems');
    });
  }

  // Initial load
  loadMcqQuestions('all', 'all');
}

async function loadMcqQuestions(lang = state.mcqLang, difficulty = state.mcqDifficulty) {
  state.mcqLang = lang || 'all';
  state.mcqDifficulty = difficulty || 'all';

  const card = document.getElementById('mcq-card');
  const summaryCard = document.getElementById('mcq-summary-card');
  if (card) card.style.display = 'block';
  if (summaryCard) summaryCard.style.display = 'none';

  const qText = document.getElementById('mcq-question-text');
  if (qText) qText.textContent = `Fetching dynamic ${state.mcqDifficulty !== 'all' ? state.mcqDifficulty : ''} questions from API...`;

  const codeBox = document.getElementById('mcq-code-snippet');
  if (codeBox) codeBox.style.display = 'none';
  const optionsContainer = document.getElementById('mcq-options-container');
  if (optionsContainer) optionsContainer.innerHTML = '';
  const explanationBox = document.getElementById('mcq-explanation');
  if (explanationBox) explanationBox.style.display = 'none';
  const nextBtn = document.getElementById('mcq-next-btn');
  if (nextBtn) nextBtn.style.display = 'none';

  state.mcqIndex = 0;
  state.mcqScore = 0;
  state.mcqAnswered = false;

  try {
    const url = `/api/mcq?language=${encodeURIComponent(state.mcqLang)}&difficulty=${encodeURIComponent(state.mcqDifficulty)}&count=5`;
    const res = await fetch(url);
    if (res.ok) {
      state.mcqQuestions = await res.json();
      if (!state.mcqQuestions || state.mcqQuestions.length === 0) {
        if (qText) qText.textContent = `No ${state.mcqDifficulty} questions available right now for this selection. Try switching difficulty or selecting "All Languages".`;
      } else {
        renderMcqQuestion();
      }
    } else {
      if (qText) qText.textContent = 'Failed to load questions. Please try again.';
    }
  } catch (err) {
    if (qText) qText.textContent = 'Error connecting to question API: ' + err.message;
  }
}

function renderMcqQuestion() {
  if (!state.mcqQuestions || state.mcqQuestions.length === 0) return;

  const q = state.mcqQuestions[state.mcqIndex];
  state.mcqAnswered = false;

  // Progress Bar & Counter
  const progressPercent = ((state.mcqIndex) / state.mcqQuestions.length) * 100;
  const progressFill = document.getElementById('mcq-progress-fill');
  if (progressFill) progressFill.style.width = `${progressPercent}%`;

  document.getElementById('mcq-q-counter').textContent = `Question ${state.mcqIndex + 1} of ${state.mcqQuestions.length}`;
  document.getElementById('mcq-current-score').textContent = `Score: ${state.mcqScore} / ${state.mcqQuestions.length}`;

  // Badges
  const langBadge = document.getElementById('mcq-lang-badge');
  langBadge.textContent = (q.language || 'Code').toUpperCase();

  const diffBadge = document.getElementById('mcq-diff-badge');
  diffBadge.textContent = q.difficulty || 'Medium';
  diffBadge.className = `badge ${q.difficulty === 'Easy' ? 'badge-easy' : (q.difficulty === 'Hard' ? 'badge-hard' : 'badge-medium')}`;

  // Question Title & Code
  document.getElementById('mcq-question-text').textContent = q.question;

  const codeBox = document.getElementById('mcq-code-snippet');
  if (q.codeSnippet && q.codeSnippet.trim()) {
    codeBox.style.display = 'block';
    codeBox.textContent = q.codeSnippet;
  } else {
    codeBox.style.display = 'none';
  }

  // Options
  const optionsContainer = document.getElementById('mcq-options-container');
  optionsContainer.innerHTML = '';
  const letters = ['A', 'B', 'C', 'D'];

  q.options.forEach((opt, idx) => {
    const btn = document.createElement('button');
    btn.className = 'mcq-option-btn';
    btn.innerHTML = `
      <div class="mcq-option-letter">${letters[idx] || (idx + 1)}</div>
      <div style="flex: 1;">${escapeHtml(opt)}</div>
    `;

    btn.addEventListener('click', () => {
      if (!state.mcqAnswered) {
        handleOptionClick(btn, idx, q);
      }
    });

    optionsContainer.appendChild(btn);
  });

  // Hide explanation & next button
  document.getElementById('mcq-explanation').style.display = 'none';
  document.getElementById('mcq-next-btn').style.display = 'none';
}

function handleOptionClick(selectedBtn, chosenIndex, question) {
  state.mcqAnswered = true;
  const optionsContainer = document.getElementById('mcq-options-container');
  const allBtns = optionsContainer.querySelectorAll('.mcq-option-btn');

  // Disable all buttons
  allBtns.forEach(b => b.disabled = true);

  const isCorrect = chosenIndex === question.correctIndex;
  if (isCorrect) {
    selectedBtn.classList.add('correct');
    state.mcqScore++;
  } else {
    selectedBtn.classList.add('incorrect');
    // Highlight correct option
    if (allBtns[question.correctIndex]) {
      allBtns[question.correctIndex].classList.add('correct');
    }
  }

  // Update Score Counter
  document.getElementById('mcq-current-score').textContent = `Score: ${state.mcqScore} / ${state.mcqQuestions.length}`;

  // Display Explanation
  const explanationBox = document.getElementById('mcq-explanation');
  explanationBox.style.display = 'block';
  explanationBox.innerHTML = `
    <div style="font-weight: 700; margin-bottom: 0.35rem; color: ${isCorrect ? 'var(--accent-green)' : '#fda4af'};">
      ${isCorrect ? '✓ Correct Answer!' : '✗ Incorrect!'}
    </div>
    <div>${escapeHtml(question.explanation)}</div>
  `;

  // Show Next Button
  const nextBtn = document.getElementById('mcq-next-btn');
  nextBtn.style.display = 'inline-flex';
  nextBtn.querySelector('span:first-child').textContent = state.mcqIndex + 1 === state.mcqQuestions.length ? 'View Results' : 'Next Question';
}

function finishMcqQuiz() {
  document.getElementById('mcq-card').style.display = 'none';
  const summaryCard = document.getElementById('mcq-summary-card');
  summaryCard.style.display = 'block';

  const progressFill = document.getElementById('mcq-progress-fill');
  if (progressFill) progressFill.style.width = '100%';

  const total = state.mcqQuestions.length;
  const score = state.mcqScore;
  const percent = Math.round((score / total) * 100);

  document.getElementById('mcq-summary-title').textContent = percent >= 80 ? '🎉 Exceptional Mastery!' : (percent >= 50 ? '👏 Good Job!' : '📚 Keep Practicing!');
  document.getElementById('mcq-summary-subtitle').innerHTML = `
    You scored <strong>${score} out of ${total}</strong> (${percent}%) in ${state.mcqLang.toUpperCase()} MCQs.<br>
    <span style="font-size: 0.95rem; color: var(--accent-cyan);">+${score * 10} XP points added to your Codely profile!</span>
  `;
}

// ==================== COURSES ====================
async function loadCourses() {
  try {
    const res = await fetch('/api/courses');
    if (res.ok) {
      const courses = await res.json();
      renderCourses(courses);
    }
  } catch (err) {
    console.error('Error loading courses:', err);
  }
}

function renderCourses(courses) {
  const container = document.getElementById('courses-list-container');
  container.innerHTML = '';

  courses.forEach((c, idx) => {
    const trackBox = document.createElement('div');
    trackBox.className = `course-track-item ${idx === 0 ? 'active' : ''}`;
    trackBox.innerHTML = `
      <div style="display: flex; align-items: center; gap: 0.5rem; font-weight: 700; margin-bottom: 0.35rem;">
        <span style="font-size: 1.2rem;">${c.icon || '📘'}</span>
        <span>${escapeHtml(c.title)}</span>
      </div>
      <div style="font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.6rem;">${escapeHtml(c.level)}</div>
      <div class="chapters-sublist" id="chapters-for-${c.id}"></div>
    `;

    const chapContainer = trackBox.querySelector(`#chapters-for-${c.id}`);
    c.chapters.forEach((ch, chIdx) => {
      const chItem = document.createElement('div');
      chItem.className = `chapter-item ${idx === 0 && chIdx === 0 ? 'active' : ''}`;
      chItem.innerHTML = `<span>📖</span> <span>${escapeHtml(ch.title)}</span>`;
      chItem.addEventListener('click', (e) => {
        e.stopPropagation();
        container.querySelectorAll('.chapter-item').forEach(el => el.classList.remove('active'));
        container.querySelectorAll('.course-track-item').forEach(el => el.classList.remove('active'));
        trackBox.classList.add('active');
        chItem.classList.add('active');
        renderChapterContent(c, ch);
      });
      chapContainer.appendChild(chItem);
    });

    trackBox.addEventListener('click', () => {
      if (c.chapters.length > 0) {
        container.querySelectorAll('.course-track-item').forEach(el => el.classList.remove('active'));
        trackBox.classList.add('active');
        const firstCh = chapContainer.querySelector('.chapter-item');
        if (firstCh) firstCh.click();
      }
    });

    container.appendChild(trackBox);
  });

  // Render initial chapter
  if (courses.length > 0 && courses[0].chapters.length > 0) {
    renderChapterContent(courses[0], courses[0].chapters[0]);
  }
}

function renderChapterContent(course, chapter) {
  const pane = document.getElementById('course-content-pane');
  pane.innerHTML = `
    <div style="display: flex; align-items: center; gap: 0.5rem; color: var(--accent-cyan); font-size: 0.9rem; font-weight: 600; margin-bottom: 0.5rem;">
      <span>${course.icon}</span> <span>${escapeHtml(course.title)}</span>
    </div>
    <h1>${escapeHtml(chapter.title)}</h1>
    <div style="margin-top: 1.5rem;">
      ${formatMarkdown(chapter.content)}
    </div>

    <div class="try-lab-card">
      <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 1rem;">
        <div style="font-weight: 700; font-size: 1.1rem; display: flex; align-items: center; gap: 0.5rem;">
          <span>💻</span> CodeTantra Interactive Lab Challenge
        </div>
        <span class="badge badge-tag">${chapter.language.toUpperCase()}</span>
      </div>
      <p style="font-size: 0.9rem; color: var(--text-secondary); margin-bottom: 1rem;">
        Click below to load this code directly into your CodeTantra multi-language compiler and execute it with automated feedback.
      </p>
      <pre class="code-snippet-box" style="margin-bottom: 1.25rem;">${escapeHtml(chapter.starterCode)}</pre>
      <button class="btn btn-primary" id="open-chapter-lab-btn">
        <span>🚀</span> Open & Run in Lab
      </button>
    </div>
  `;

  document.getElementById('open-chapter-lab-btn').addEventListener('click', () => {
    // Switch to Playground
    switchView('tab-playground', 'view-playground');
    const select = document.getElementById('playground-lang-select');
    select.value = chapter.language;
    state.playgroundLang = chapter.language;
    setEditorCode(chapter.starterCode, chapter.language, false);
    setTimeout(() => {
      document.getElementById('playground-run-btn').click();
    }, 200);
  });
}

// ==================== ASSESSMENTS ====================
async function loadAssessments() {
  try {
    const res = await fetch('/api/assessments');
    if (res.ok) {
      const assessments = await res.json();
      renderAssessments(assessments);
    }
  } catch (err) {
    console.error('Error loading assessments:', err);
  }
}

function renderAssessments(assessments) {
  const container = document.getElementById('assessments-cards-grid');
  container.innerHTML = '';

  assessments.forEach(a => {
    const card = document.createElement('div');
    card.className = 'section-box';
    card.style.display = 'flex';
    card.style.flexDirection = 'column';
    card.style.justifyContent = 'space-between';
    card.innerHTML = `
      <div>
        <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 0.75rem;">
          <span class="badge badge-medium">Timed Lab Exam</span>
          <span style="font-size: 0.85rem; color: var(--accent-amber); font-weight: 700;">⏱️ ${a.durationMinutes} Mins</span>
        </div>
        <h3 style="font-size: 1.2rem; margin-bottom: 0.5rem; font-weight: 700;">${escapeHtml(a.title)}</h3>
        <p style="font-size: 0.9rem; color: var(--text-secondary); line-height: 1.6; margin-bottom: 1rem;">
          ${escapeHtml(a.description)}
        </p>
        <div style="font-size: 0.85rem; color: var(--text-muted); margin-bottom: 1.25rem;">
          📋 <strong>${a.problemIds ? a.problemIds.length : 0} Questions</strong> | 🏆 <strong>${a.totalMarks} Marks</strong>
        </div>
      </div>
      <div>
        <button class="btn btn-primary start-exam-btn" data-id="${a.id}" style="width: 100%; justify-content: center;">
          <span>📝</span> Start Lab Exam
        </button>
      </div>
    `;

    card.querySelector('.start-exam-btn').addEventListener('click', () => {
      startAssessmentExam(a);
    });

    container.appendChild(card);
  });
}

function startAssessmentExam(assessment) {
  state.assessmentExam = assessment;
  state.assessmentIndex = 0;
  state.assessmentAnswers = {};
  state.examSecondsLeft = assessment.durationMinutes * 60;

  document.getElementById('assessments-catalog-container').style.display = 'none';
  const liveContainer = document.getElementById('live-assessment-container');
  liveContainer.style.display = 'flex';

  document.getElementById('exam-title').textContent = assessment.title;

  // Question Palette
  renderExamPalette();

  // Timer
  if (state.examTimerInterval) clearInterval(state.examTimerInterval);
  updateExamClock();
  state.examTimerInterval = setInterval(() => {
    state.examSecondsLeft--;
    updateExamClock();
    if (state.examSecondsLeft <= 0) {
      clearInterval(state.examTimerInterval);
      finishAssessmentExam();
    }
  }, 1000);

  // Load first problem
  loadExamQuestion(0);

  // Finish exam button
  document.getElementById('finish-exam-btn').onclick = finishAssessmentExam;
}

function updateExamClock() {
  const mins = Math.floor(state.examSecondsLeft / 60);
  const secs = state.examSecondsLeft % 60;
  document.getElementById('exam-clock').textContent = `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
}

function renderExamPalette() {
  const palette = document.getElementById('exam-question-palette');
  palette.innerHTML = '';
  const a = state.assessmentExam;
  a.problemIds.forEach((pid, idx) => {
    const dot = document.createElement('div');
    dot.className = `palette-dot ${idx === state.assessmentIndex ? 'active' : ''} ${state.assessmentAnswers[pid] && state.assessmentAnswers[pid].passed ? 'solved' : ''}`;
    dot.textContent = idx + 1;
    dot.title = `Question ${idx + 1}`;
    dot.addEventListener('click', () => {
      state.assessmentIndex = idx;
      renderExamPalette();
      loadExamQuestion(idx);
    });
    palette.appendChild(dot);
  });
}

function loadExamQuestion(idx) {
  const pid = state.assessmentExam.problemIds[idx];
  openProblem(pid);

  // Move the problem workbench into the exam wrapper
  const wb = document.getElementById('problem-workbench-container');
  const wrapper = document.getElementById('exam-workbench-wrapper');
  wrapper.appendChild(wb);
  wb.style.display = 'flex';

  // Override submit behavior in exam mode to mark palette
  const origSubmitBtn = document.getElementById('workbench-submit-btn');
  const newSubmitBtn = origSubmitBtn.cloneNode(true);
  origSubmitBtn.parentNode.replaceChild(newSubmitBtn, origSubmitBtn);

  newSubmitBtn.addEventListener('click', async () => {
    const code = getEditorCode(true);
    const resultsSummary = document.getElementById('wb-results-summary');
    const verdictTitle = document.getElementById('wb-verdict-title');
    const timeSummary = document.getElementById('wb-time-summary');
    const testCasesList = document.getElementById('wb-test-cases-list');

    document.querySelector('[data-target="wb-tab-testcases"]').click();
    newSubmitBtn.disabled = true;
    newSubmitBtn.innerHTML = '<span class="spinner"></span> Grading...';

    try {
      const res = await fetch('/api/submit', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          problemId: pid,
          language: state.workbenchLang,
          code: code
        })
      });

      const report = await res.json();
      resultsSummary.style.display = 'flex';
      timeSummary.textContent = `Execution Time: ${report.totalTimeMs} ms`;

      if (report.allPassed) {
        verdictTitle.className = 'results-verdict passed';
        verdictTitle.innerHTML = `🎉 Accepted (${report.passedTestCases}/${report.totalTestCases} Passed)`;
        state.assessmentAnswers[pid] = { passed: true, code, language: state.workbenchLang };
      } else {
        verdictTitle.className = 'results-verdict failed';
        verdictTitle.innerHTML = `❌ ${escapeHtml(report.verdict)} (${report.passedTestCases}/${report.totalTestCases} Passed)`;
        state.assessmentAnswers[pid] = { passed: false, code, language: state.workbenchLang };
      }

      renderExamPalette();

      // Render cards
      testCasesList.innerHTML = '';
      if (report.testResults) {
        report.testResults.forEach(tr => {
          const card = document.createElement('div');
          card.className = `test-case-card ${tr.passed ? 'passed' : 'failed'}`;
          card.innerHTML = `
            <div class="tc-header">
              <span>Test Case #${tr.testCaseNumber}</span>
              <span style="color: ${tr.passed ? 'var(--accent-green)' : 'var(--accent-rose)'}; font-weight: 700;">
                ${tr.passed ? 'Passed ✓' : 'Failed ✗'}
              </span>
            </div>
            <div class="tc-field"><div class="tc-field-label">Expected:</div><div class="tc-field-val">${escapeHtml(tr.expectedOutput)}</div></div>
            <div class="tc-field"><div class="tc-field-label">Actual:</div><div class="tc-field-val">${escapeHtml(tr.actualOutput)}</div></div>
          `;
          testCasesList.appendChild(card);
        });
      }
    } catch (e) {
      testCasesList.innerHTML = `<div style="color: var(--accent-rose);">${e.message}</div>`;
    } finally {
      newSubmitBtn.disabled = false;
      newSubmitBtn.innerHTML = '<span>🚀</span> Submit Solution';
    }
  });
}

function finishAssessmentExam() {
  if (state.examTimerInterval) clearInterval(state.examTimerInterval);

  let passedCount = 0;
  const a = state.assessmentExam;
  a.problemIds.forEach(pid => {
    if (state.assessmentAnswers[pid] && state.assessmentAnswers[pid].passed) {
      passedCount++;
    }
  });

  const score = Math.round((passedCount / a.problemIds.length) * a.totalMarks);

  document.getElementById('exam-final-score').textContent = `Score: ${score} / ${a.totalMarks}`;
  document.getElementById('exam-final-verdict').textContent = `You solved ${passedCount} out of ${a.problemIds.length} exam problems correctly!`;

  const modal = document.getElementById('exam-score-modal');
  modal.classList.add('active');

  document.getElementById('exit-exam-btn').onclick = () => {
    modal.classList.remove('active');
    document.getElementById('live-assessment-container').style.display = 'none';
    document.getElementById('assessments-catalog-container').style.display = 'block';

    // Move workbench back
    const wb = document.getElementById('problem-workbench-container');
    document.getElementById('view-problems').appendChild(wb);
    wb.style.display = 'none';
    document.getElementById('problems-catalog-container').style.display = 'block';
  };
}

// ==================== DASHBOARD ====================
async function loadDashboard() {
  try {
    const res = await fetch('/api/dashboard');
    if (res.ok) {
      const data = await res.json();
      renderDashboard(data);
    }
  } catch (err) {
    console.error('Error loading dashboard:', err);
  }
}

function renderDashboard(data) {
  document.getElementById('dash-solved-count').textContent = `${data.solvedCount || 0} / ${data.totalProblems || 5}`;
  document.getElementById('dash-streak-count').textContent = `${data.streak || 4} Days`;
  document.getElementById('dash-xp-count').textContent = `${data.totalXp || 0} XP`;
  document.getElementById('dash-subs-count').textContent = `${data.totalSubmissions || 0}`;

  document.getElementById('user-streak-badge').textContent = data.streak || 4;
  document.getElementById('user-xp-badge').textContent = data.totalXp || 0;

  if (data.solvedProblems) {
    state.solvedProblemIds = new Set(data.solvedProblems);
  }

  // Language stats
  const langContainer = document.getElementById('dash-languages-container');
  langContainer.innerHTML = '';
  if (data.languageStats) {
    for (const [lang, count] of Object.entries(data.languageStats)) {
      const pill = document.createElement('div');
      pill.className = 'stats-badge';
      pill.innerHTML = `<strong>${lang.toUpperCase()}:</strong> ${count} submissions`;
      langContainer.appendChild(pill);
    }
  }

  // Recent Submissions
  const subsTable = document.getElementById('dash-submissions-table');
  subsTable.innerHTML = '';
  if (!data.recentSubmissions || data.recentSubmissions.length === 0) {
    subsTable.innerHTML = `<tr><td colspan="7" style="text-align: center; color: var(--text-muted); padding: 2rem;">No submissions yet. Start solving problems!</td></tr>`;
    return;
  }

  data.recentSubmissions.forEach(sub => {
    const tr = document.createElement('tr');
    const isAcc = sub.verdict === 'Accepted';
    tr.innerHTML = `
      <td><strong>${escapeHtml(sub.problemTitle)}</strong></td>
      <td><span class="badge badge-tag">${sub.language.toUpperCase()}</span></td>
      <td>
        <span class="badge ${isAcc ? 'badge-easy' : 'badge-hard'}">
          ${escapeHtml(sub.verdict)}
        </span>
      </td>
      <td>${sub.passedCases}/${sub.totalCases}</td>
      <td>${sub.executionTimeMs} ms</td>
      <td style="color: var(--text-muted); font-size: 0.85rem;">${escapeHtml(sub.timestamp)}</td>
      <td>
        <button class="btn btn-secondary view-code-btn" data-id="${sub.id}" style="padding: 0.25rem 0.6rem; font-size: 0.78rem;">
          View Code
        </button>
      </td>
    `;

    tr.querySelector('.view-code-btn').addEventListener('click', () => {
      openCodeModal(sub);
    });

    subsTable.appendChild(tr);
  });
}

// ==================== AUTHENTICATION MODULE ====================
function setupAuthEvents() {
  const authModal = document.getElementById('auth-modal');
  const loginBtn = document.getElementById('nav-login-btn');
  const closeBtn = document.getElementById('close-auth-modal-btn');
  const tabLogin = document.getElementById('tab-login-btn');
  const tabSignup = document.getElementById('tab-signup-btn');
  const loginForm = document.getElementById('auth-login-form');
  const signupForm = document.getElementById('auth-signup-form');
  const modalTitle = document.getElementById('auth-modal-title');
  const quickDemoBtn = document.getElementById('quick-demo-btn');
  const profileToggle = document.getElementById('user-profile-toggle');
  const dropdown = document.getElementById('user-dropdown');
  const logoutBtn = document.getElementById('dropdown-logout-btn');
  const dashBtn = document.getElementById('dropdown-dashboard-btn');

  // Open modal
  if (loginBtn) {
    loginBtn.addEventListener('click', () => {
      openAuthModal('login');
    });
  }

  // Close modal
  if (closeBtn) {
    closeBtn.addEventListener('click', () => {
      authModal.classList.remove('active');
    });
  }
  authModal.addEventListener('click', (e) => {
    if (e.target === authModal) authModal.classList.remove('active');
  });

  // Switch tabs
  if (tabLogin && tabSignup) {
    tabLogin.addEventListener('click', () => openAuthModal('login'));
    tabSignup.addEventListener('click', () => openAuthModal('signup'));
  }

  // Quick Demo Login
  if (quickDemoBtn) {
    quickDemoBtn.addEventListener('click', async () => {
      document.getElementById('login-identifier').value = 'demo@codely.dev';
      document.getElementById('login-password').value = 'codely123';
      await performLogin('demo@codely.dev', 'codely123');
    });
  }

  // Login Form Submit
  if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const id = document.getElementById('login-identifier').value.trim();
      const pass = document.getElementById('login-password').value;
      await performLogin(id, pass);
    });
  }

  // Sign Up Form Submit
  if (signupForm) {
    signupForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const name = document.getElementById('signup-name').value.trim();
      const username = document.getElementById('signup-username').value.trim();
      const email = document.getElementById('signup-email').value.trim();
      const password = document.getElementById('signup-password').value;
      const confirmPass = document.getElementById('signup-password-confirm').value;

      if (password !== confirmPass) {
        showAuthBanner('Passwords do not match.', true);
        return;
      }

      const submitBtn = document.getElementById('signup-submit-btn');
      submitBtn.disabled = true;
      submitBtn.innerHTML = '<span class="spinner"></span> Creating Account...';

      try {
        const res = await fetch('/api/auth/signup', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ name, username, email, password })
        });
        const data = await res.json();

        if (res.ok && data.token) {
          state.user = data;
          state.token = data.token;
          localStorage.setItem('codely_token', data.token);
          updateUserUI(data);
          showAuthBanner('🎉 Account created successfully! Welcome to Codely.', false);
          setTimeout(() => {
            authModal.classList.remove('active');
            signupForm.reset();
          }, 1200);
        } else {
          showAuthBanner(data.error || 'Failed to create account.', true);
        }
      } catch (err) {
        showAuthBanner('Server communication error: ' + err.message, true);
      } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<span>Create Account</span> <span>✨</span>';
      }
    });
  }

  // Toggle user profile dropdown
  if (profileToggle && dropdown) {
    profileToggle.addEventListener('click', (e) => {
      e.stopPropagation();
      const isOpen = dropdown.style.display === 'block';
      dropdown.style.display = isOpen ? 'none' : 'block';
    });

    document.addEventListener('click', () => {
      dropdown.style.display = 'none';
    });
  }

  // Dropdown navigation
  if (dashBtn) {
    dashBtn.addEventListener('click', () => {
      switchView('tab-dashboard', 'view-dashboard');
      if (dropdown) dropdown.style.display = 'none';
    });
  }

  // Logout button
  if (logoutBtn) {
    logoutBtn.addEventListener('click', async () => {
      if (state.token) {
        try {
          await fetch('/api/auth/logout', {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${state.token}` }
          });
        } catch (ignored) {}
      }
      state.user = null;
      state.token = null;
      localStorage.removeItem('codely_token');
      updateUserUI(null);
      if (dropdown) dropdown.style.display = 'none';
    });
  }
}

function openAuthModal(mode = 'login') {
  const authModal = document.getElementById('auth-modal');
  const tabLogin = document.getElementById('tab-login-btn');
  const tabSignup = document.getElementById('tab-signup-btn');
  const loginForm = document.getElementById('auth-login-form');
  const signupForm = document.getElementById('auth-signup-form');
  const modalTitle = document.getElementById('auth-modal-title');
  const banner = document.getElementById('auth-banner');

  if (banner) banner.style.display = 'none';

  if (mode === 'login') {
    tabLogin.classList.add('active');
    tabSignup.classList.remove('active');
    loginForm.style.display = 'block';
    signupForm.style.display = 'none';
    modalTitle.textContent = 'Sign in to Codely';
  } else {
    tabLogin.classList.remove('active');
    tabSignup.classList.add('active');
    loginForm.style.display = 'none';
    signupForm.style.display = 'block';
    modalTitle.textContent = 'Create your Codely Account';
  }

  authModal.classList.add('active');
}

async function performLogin(identifier, password) {
  const submitBtn = document.getElementById('login-submit-btn');
  const authModal = document.getElementById('auth-modal');
  submitBtn.disabled = true;
  submitBtn.innerHTML = '<span class="spinner"></span> Signing in...';

  try {
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ identifier, password })
    });
    const data = await res.json();

    if (res.ok && data.token) {
      state.user = data;
      state.token = data.token;
      localStorage.setItem('codely_token', data.token);
      updateUserUI(data);
      showAuthBanner('✓ Signed in successfully!', false);
      setTimeout(() => {
        authModal.classList.remove('active');
        document.getElementById('auth-login-form').reset();
      }, 800);
    } else {
      showAuthBanner(data.error || 'Invalid credentials.', true);
    }
  } catch (err) {
    showAuthBanner('Connection error: ' + err.message, true);
  } finally {
    submitBtn.disabled = false;
    submitBtn.innerHTML = '<span>Sign In</span> <span>→</span>';
  }
}

async function initAuth() {
  if (state.token) {
    try {
      const res = await fetch('/api/auth/me', {
        headers: { 'Authorization': `Bearer ${state.token}` }
      });
      if (res.ok) {
        state.user = await res.json();
        updateUserUI(state.user);
        return;
      }
    } catch (ignored) {}
    // If token invalid, clear
    state.user = null;
    state.token = null;
    localStorage.removeItem('codely_token');
  }
  updateUserUI(null);
}

function updateUserUI(user) {
  const loginBtn = document.getElementById('nav-login-btn');
  const profileMenu = document.getElementById('user-profile-menu');
  const userName = document.getElementById('nav-user-name');
  const userAvatar = document.getElementById('nav-user-avatar');
  const dropName = document.getElementById('dropdown-name');
  const dropEmail = document.getElementById('dropdown-email');
  const streakBadge = document.getElementById('user-streak-badge');
  const xpBadge = document.getElementById('user-xp-badge');

  if (user) {
    if (loginBtn) loginBtn.style.display = 'none';
    if (profileMenu) profileMenu.style.display = 'block';
    if (userName) userName.textContent = user.name.split(' ')[0] || user.name;
    if (userAvatar) userAvatar.textContent = (user.name[0] || 'U').toUpperCase();
    if (dropName) dropName.textContent = user.name;
    if (dropEmail) dropEmail.textContent = user.email;
    if (streakBadge) streakBadge.textContent = user.streak || 4;
    if (xpBadge) xpBadge.textContent = user.xp || 120;
  } else {
    if (loginBtn) loginBtn.style.display = 'inline-flex';
    if (profileMenu) profileMenu.style.display = 'none';
  }
}

function showAuthBanner(message, isError) {
  const banner = document.getElementById('auth-banner');
  if (!banner) return;
  banner.style.display = 'block';
  banner.textContent = message;
  if (isError) {
    banner.style.background = 'rgba(244, 63, 94, 0.15)';
    banner.style.border = '1px solid rgba(244, 63, 94, 0.3)';
    banner.style.color = '#fda4af';
  } else {
    banner.style.background = 'rgba(16, 185, 129, 0.15)';
    banner.style.border = '1px solid rgba(16, 185, 129, 0.3)';
    banner.style.color = '#a7f3d0';
  }
}

function openCodeModal(sub) {
  document.getElementById('code-modal-title').textContent = `${sub.problemTitle} (${sub.language.toUpperCase()})`;
  document.getElementById('code-modal-content').textContent = sub.code || '// No code saved';
  document.getElementById('code-modal').classList.add('active');
}

function setupModalEvents() {
  const modal = document.getElementById('code-modal');
  document.getElementById('close-code-modal-btn').addEventListener('click', () => modal.classList.remove('active'));
  document.getElementById('close-code-modal-btn2').addEventListener('click', () => modal.classList.remove('active'));
  modal.addEventListener('click', (e) => {
    if (e.target === modal) modal.classList.remove('active');
  });

  document.getElementById('dashboard-reset-btn').addEventListener('click', async () => {
    if (confirm('Are you sure you want to reset your dashboard progress?')) {
      await fetch('/api/reset', { method: 'POST' });
      await loadDashboard();
    }
  });
}

// ==================== UTILITIES ====================
function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

function formatMarkdown(text) {
  if (!text) return '';
  let html = escapeHtml(text);

  // Headers
  html = html.replace(/^### (.*$)/gim, '<h3 style="margin: 1rem 0 0.5rem 0; font-size: 1.1rem; color: var(--accent-cyan);">$1</h3>');
  html = html.replace(/^## (.*$)/gim, '<h2 style="margin: 1.25rem 0 0.5rem 0; font-size: 1.25rem; color: var(--accent-cyan);">$1</h2>');

  // Bold & Italics
  html = html.replace(/\*\*(.*?)\*\*/gim, '<strong>$1</strong>');
  html = html.replace(/\*(.*?)\*/gim, '<em>$1</em>');

  // Inline code
  html = html.replace(/`([^`]+)`/gim, '<code style="background: var(--bg-tertiary); color: var(--accent-cyan); padding: 0.15rem 0.35rem; border-radius: 4px; font-family: var(--font-mono); font-size: 0.85rem;">$1</code>');

  // Line breaks
  html = html.replace(/\n\n/g, '</p><p style="margin-bottom: 0.75rem;">');
  html = '<p style="margin-bottom: 0.75rem;">' + html + '</p>';
  return html;
}
