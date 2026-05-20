'use strict';

const UI_LABELS = {
  tc: {
    projectsLabel: '專案經歷 Projects',
    sections: {
      workExperience: '工作經歷 Work Experience',
      sideProjects: '個人研究 Side Projects',
      technicalSkills: '技術技能 Technical Skills',
      education: '學歷 Education',
      languages: '語言能力 Languages',
      about: '自傳 About Me',
    },
    skillNames: {
      'ai-llm': 'AI / LLM',
      'architecture': '架構 / 原則',
      'kmp': 'Kotlin Multiplatform',
      'android': 'Android',
      'ios': 'iOS',
      'tools': '通用 / 工具',
    },
    language: {
      names: { zh: '中文', en: '英文' },
      native: '母語',
      axes: { listening: '聽', speaking: '說', reading: '讀', writing: '寫' },
      skills: { basic: '基礎', intermediate: '中級', advanced: '進階', fluent: '流利' },
    },
  },
  en: {
    projectsLabel: 'Projects',
    sections: {
      workExperience: 'Work Experience',
      sideProjects: 'Side Projects',
      technicalSkills: 'Technical Skills',
      education: 'Education',
      languages: 'Languages',
      about: 'About Me',
    },
    skillNames: {
      'ai-llm': 'AI / LLM',
      'architecture': 'Architecture / Principles',
      'kmp': 'Kotlin Multiplatform',
      'android': 'Android',
      'ios': 'iOS',
      'tools': 'General / Tools',
    },
    language: {
      names: { zh: 'Chinese', en: 'English' },
      native: 'Native',
      axes: { listening: 'Listen', speaking: 'Speak', reading: 'Read', writing: 'Write' },
      skills: { basic: 'Basic', intermediate: 'Intermediate', advanced: 'Advanced', fluent: 'Fluent' },
    },
  },
};

const SVG_NS = 'http://www.w3.org/2000/svg';

const SVG_ICONS = {
  email: 'M20 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z',
  phone: 'M6.62 10.79a15.05 15.05 0 006.59 6.59l2.2-2.2a1 1 0 011.01-.24c1.12.37 2.33.57 3.58.57.55 0 1 .45 1 1V20a1 1 0 01-1 1A17 17 0 013 4a1 1 0 011-1h3.5a1 1 0 011 1c0 1.25.2 2.46.57 3.58a1 1 0 01-.24 1.01l-2.2 2.2z',
  github: 'M12 .5C5.65.5.5 5.65.5 12c0 5.08 3.29 9.39 7.86 10.91.58.11.79-.25.79-.55v-2.07c-3.2.7-3.88-1.36-3.88-1.36-.52-1.33-1.28-1.69-1.28-1.69-1.05-.72.08-.7.08-.7 1.16.08 1.77 1.19 1.77 1.19 1.03 1.77 2.71 1.26 3.37.96.1-.74.4-1.26.73-1.55-2.55-.29-5.24-1.28-5.24-5.69 0-1.26.45-2.28 1.18-3.09-.12-.29-.51-1.46.11-3.04 0 0 .97-.31 3.18 1.18a11.04 11.04 0 0 1 5.79 0c2.21-1.49 3.18-1.18 3.18-1.18.62 1.58.23 2.75.11 3.04.74.81 1.18 1.83 1.18 3.09 0 4.42-2.7 5.4-5.27 5.68.41.36.78 1.06.78 2.15v3.18c0 .31.21.67.79.55C20.21 21.39 23.5 17.08 23.5 12 23.5 5.65 18.35.5 12 .5z',
  linkedin: 'M19 0h-14c-2.761 0-5 2.239-5 5v14c0 2.761 2.239 5 5 5h14c2.761 0 5-2.239 5-5v-14c0-2.761-2.239-5-5-5zm-11 19h-3v-11h3v11zm-1.5-12.268c-.966 0-1.75-.79-1.75-1.764s.784-1.764 1.75-1.764 1.75.79 1.75 1.764-.783 1.764-1.75 1.764zm13.5 12.268h-3v-5.604c0-3.368-4-3.113-4 0v5.604h-3v-11h3v1.765c1.396-2.586 7-2.777 7 2.476v6.759z',
};

function el(tag, className, content) {
  const e = document.createElement(tag);
  if (className) e.className = className;
  if (content == null) return e;
  if (typeof content === 'string') e.textContent = content;
  else if (Array.isArray(content)) content.forEach(c => c && e.appendChild(c));
  else if (content instanceof Node) e.appendChild(content);
  return e;
}

function svgIcon(type) {
  const path = SVG_ICONS[type];
  if (!path) return null;
  const svg = document.createElementNS(SVG_NS, 'svg');
  svg.setAttribute('class', 'icon');
  svg.setAttribute('viewBox', '0 0 24 24');
  const p = document.createElementNS(SVG_NS, 'path');
  p.setAttribute('d', path);
  svg.appendChild(p);
  return svg;
}

function bulletList(bullets, ulClass) {
  const ul = el('ul', ulClass);
  bullets.forEach(b => ul.appendChild(el('li', null, b)));
  return ul;
}

function tagList(tags, wrapClass, itemClass) {
  const wrap = el('div', wrapClass);
  tags.forEach(t => wrap.appendChild(el('span', itemClass, t)));
  return wrap;
}

const EN_MONTH_SHORT = [
  'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
  'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec',
];

function parseYearMonth(s) {
  if (!s) return null;
  const [y, m] = s.split('-');
  return { year: Number(y), month: Number(m) };
}

function formatYearMonth(ym, lang) {
  if (lang === 'tc') return `${ym.year}/${String(ym.month).padStart(2, '0')}`;
  return `${EN_MONTH_SHORT[ym.month - 1]} ${ym.year}`;
}

function periodLabel(period) {
  if (!period) return '';
  const lang = document.documentElement.lang === 'zh-Hant' ? 'tc' : 'en';
  const start = parseYearMonth(period.start);
  const end = parseYearMonth(period.end);
  if (!start) return '';

  // Year-only display when start is January and end is either December or open-ended
  const startsInJan = start.month === 1;
  const endsInDec = end != null && end.month === 12;
  if (startsInJan && (endsInDec || end == null)) {
    if (end == null || start.year === end.year) return `${start.year}`;
    return `${start.year} ─ ${end.year}`;
  }

  const present = lang === 'tc' ? '至今' : 'Present';
  const startLabel = formatYearMonth(start, lang);
  const endLabel = end ? formatYearMonth(end, lang) : present;
  return `${startLabel} ─ ${endLabel}`;
}

function renderHeader(data) {
  document.querySelector('.header h1').textContent = data.name;
  document.querySelector('.header .english-name').textContent = data.englishName;
  document.querySelector('.header .subtitle').textContent = data.subtitle;

  const tagline = document.querySelector('.header .tagline');
  tagline.textContent = '';
  tagline.appendChild(document.createTextNode(data.tagline.text + ' ｜ '));
  tagline.appendChild(el('span', 'accent', data.tagline.keywords.join('・')));

  const contactInfo = document.querySelector('.contact-info');
  contactInfo.textContent = '';
  data.contacts.forEach(c => {
    const item = el('div', 'contact-item');
    const icon = svgIcon(c.type);
    if (icon) item.appendChild(icon);
    if (c.url) {
      const a = el('a', null, c.value);
      a.href = c.url;
      if (/^https?:/.test(c.url)) a.target = '_blank';
      item.appendChild(a);
    } else {
      item.appendChild(document.createTextNode(c.value));
    }
    contactInfo.appendChild(item);
  });

  const photo = document.querySelector('.header-photo');
  photo.src = new URL(data.photo.url, `${DATA_ORIGIN}/`).href;
  photo.alt = data.photo.alt;
}

function renderSectionTitle(rootEl, titleText) {
  rootEl.textContent = '';
  rootEl.appendChild(el('h2', 'section-title', titleText));
  return rootEl;
}

function renderProject(project) {
  const wrap = el('div', 'project');

  const header = el('div', 'project-header');
  header.appendChild(el('span', 'project-name', project.name));
  header.appendChild(el('span', 'project-period', periodLabel(project.period)));
  wrap.appendChild(header);

  if (project.summary) {
    wrap.appendChild(el('div', 'project-summary', project.summary));
  }

  if (project.type === 'yearList') {
    const list = el('div', 'year-list');
    project.yearItems.forEach(yi => list.appendChild(renderYearItem(yi)));
    wrap.appendChild(list);
  } else if (project.type === 'bullets') {
    wrap.appendChild(bulletList(project.bullets, 'project-bullets'));
  }

  return wrap;
}

function renderYearItem(yi) {
  const item = el('div', 'year-item');

  const marker = el('div', 'year-marker');
  yi.years.forEach((y, i) => {
    if (i > 0) marker.appendChild(document.createElement('br'));
    marker.appendChild(document.createTextNode(y));
  });
  item.appendChild(marker);

  const content = el('div', 'year-content');

  const text = el('div', 'year-text');
  text.appendChild(el('strong', null, yi.title));
  if (yi.description) {
    text.appendChild(document.createTextNode(' — ' + yi.description));
  }
  content.appendChild(text);

  if (yi.bullets && yi.bullets.length) {
    content.appendChild(bulletList(yi.bullets, 'year-bullets'));
  }
  if (yi.tags && yi.tags.length) {
    content.appendChild(tagList(yi.tags, 'year-tags', 'year-tag'));
  }

  item.appendChild(content);
  return item;
}

function renderWorkExperience(rootEl, jobs, labels) {
  renderSectionTitle(rootEl, labels.sections.workExperience);
  jobs.forEach(job => {
    const item = el('div', 'experience-item');

    const header = el('div', 'exp-header');
    header.appendChild(el('span', 'exp-company', job.company));
    header.appendChild(el('span', 'exp-period', periodLabel(job.period)));
    item.appendChild(header);

    item.appendChild(el('div', 'exp-title', job.title));
    item.appendChild(bulletList(job.bullets, 'exp-desc'));

    if (job.projects && job.projects.length) {
      const projectsWrap = el('div', 'projects');
      projectsWrap.appendChild(el('div', 'projects-label', labels.projectsLabel));
      job.projects.forEach(p => projectsWrap.appendChild(renderProject(p)));
      item.appendChild(projectsWrap);
    }

    rootEl.appendChild(item);
  });
}

function renderSideProjects(rootEl, sideProjects, labels) {
  renderSectionTitle(rootEl, labels.sections.sideProjects);
  sideProjects.forEach(sp => {
    const item = el('div', 'experience-item');
    const header = el('div', 'exp-header');
    header.appendChild(el('span', 'exp-company', sp.name));
    header.appendChild(el('span', 'exp-period', periodLabel(sp.period)));
    item.appendChild(header);
    item.appendChild(el('div', 'exp-title', sp.subtitle));
    item.appendChild(bulletList(sp.bullets, 'exp-desc'));
    rootEl.appendChild(item);
  });
}

function renderSkills(rootEl, skills, labels) {
  renderSectionTitle(rootEl, labels.sections.technicalSkills);
  skills.forEach(group => {
    const name = labels.skillNames[group.id] || group.id;
    if (group.type === 'platform') {
      const platform = el('div', 'skills-platform');
      platform.appendChild(el('div', 'skills-platform-title', name));
      const subWrap = el('div', 'skills-subcategories');
      group.subcategories.forEach(sub => {
        const subBlock = el('div', 'skills-sub');
        subBlock.appendChild(el('div', 'skills-sub-label', sub.label));
        const ul = el('ul', 'skills-sub-list');
        sub.items.forEach(item => ul.appendChild(el('li', null, item)));
        subBlock.appendChild(ul);
        subWrap.appendChild(subBlock);
      });
      platform.appendChild(subWrap);
      rootEl.appendChild(platform);
    } else if (group.type === 'category') {
      const cat = el('div', 'skills-category');
      cat.appendChild(el('div', 'skills-label', name));
      cat.appendChild(tagList(group.tags, 'skills-tags', 'tag'));
      rootEl.appendChild(cat);
    }
  });
}

function renderEducation(rootEl, education, labels) {
  renderSectionTitle(rootEl, labels.sections.education);
  education.forEach(e => {
    const item = el('div', 'experience-item');
    const header = el('div', 'exp-header');
    header.appendChild(el('span', 'exp-company', e.school));
    header.appendChild(el('span', 'exp-period', periodLabel(e.period)));
    item.appendChild(header);
    item.appendChild(el('div', 'exp-title', e.major));
    rootEl.appendChild(item);
  });
}

function renderLanguages(rootEl, languages, labels) {
  renderSectionTitle(rootEl, labels.sections.languages);
  languages.forEach(l => {
    const item = el('div', 'lang-item');
    const name = labels.language.names[l.code] || l.code;
    item.appendChild(el('span', 'lang-name', name));
    item.appendChild(el('span', 'lang-level', formatLanguageLevel(l.level, labels.language)));
    if (l.badge) item.appendChild(el('span', 'lang-badge', l.badge));
    rootEl.appendChild(item);
  });
}

function formatLanguageLevel(level, labels) {
  if (level.kind === 'native') return labels.native;
  if (level.kind === 'proficiency') {
    return ['listening', 'speaking', 'reading', 'writing']
      .map(axis => `${labels.axes[axis]} ${labels.skills[level[axis]] || level[axis]}`)
      .join(' · ');
  }
  return '';
}

function renderVersion(rootEl) {
  rootEl.textContent = `v${window.RESUME_CONFIG.version}`;
}

function renderAbout(rootEl, paragraphs, labels) {
  renderSectionTitle(rootEl, labels.sections.about);
  const content = el('div', 'about-content');
  paragraphs.forEach(p => content.appendChild(el('p', null, p)));
  rootEl.appendChild(content);
}

const DATA_ORIGIN = window.RESUME_CONFIG.dataOrigin;
const DATA_BASE_URL = `${DATA_ORIGIN}/data`;

async function fetchJson(url) {
  const res = await fetch(url);
  if (!res.ok) throw new Error(`HTTP ${res.status} for ${url}`);
  return res.json();
}

async function loadResume(lang) {
  try {
    const base = `${DATA_BASE_URL}/${lang}`;
    const [meta, header, workExperience, sideProjects, skills, education, languages, about] =
      await Promise.all([
        fetchJson(`${base}/meta.json`),
        fetchJson(`${base}/header.json`),
        fetchJson(`${base}/work-experience.json`),
        fetchJson(`${base}/side-projects.json`),
        fetchJson(`${base}/skills.json`),
        fetchJson(`${base}/education.json`),
        fetchJson(`${base}/languages.json`),
        fetchJson(`${base}/about.json`),
      ]);

    document.title = meta.labels.pageTitle;
    const labels = UI_LABELS[lang];

    renderHeader(header);
    renderWorkExperience(document.querySelector('#work-experience'), workExperience, labels);
    renderSideProjects(document.querySelector('#side-projects'), sideProjects, labels);
    renderSkills(document.querySelector('#skills'), skills, labels);
    renderEducation(document.querySelector('#education'), education, labels);
    renderLanguages(document.querySelector('#languages'), languages, labels);
    renderAbout(document.querySelector('#about'), about, labels);
    renderVersion(document.querySelector('#version-footer'));

    document.body.classList.add('rendered');
  } catch (err) {
    console.error('Failed to load resume:', err);
    document.body.classList.add('error');
    document.body.innerHTML =
      `<pre style="padding:48px;color:#c00;font-family:monospace;white-space:pre-wrap">` +
      `Failed to load resume data.\n${err.message}` +
      `</pre>`;
  }
}

document.addEventListener('DOMContentLoaded', () => {
  const lang = document.documentElement.lang === 'zh-Hant' ? 'tc' : 'en';
  loadResume(lang);
});
