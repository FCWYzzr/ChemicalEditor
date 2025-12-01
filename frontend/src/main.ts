import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import MarkdownIt from 'markdown-it';
import MathJax from 'markdown-it-mathjax3';

// @ts-ignore
window.markdown = new MarkdownIt({
  inline: true,
  block: false,
  html: true,
  breaks: true
})
markdown.use(MathJax, {
  tex: {
    inlineMath: [['$', '$'], ['\\(', '\\)']],
    displayMath: [['$$', '$$'], ['\\[', '\\]']],
  }
})
// @ts-ignore
window.jax = MathJax;


createApp(App).mount('#app')
