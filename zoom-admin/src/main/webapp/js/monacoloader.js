 /* For now: default to cdn. */
 function loadMonaco(srcPath = '/js', callback) {
    if (window.monaco) {
      callback();
      return;
    }
    const config = {
      paths: {
        vs: srcPath + '/vs'
      }
    };
    const loaderUrl = `${config.paths.vs}/loader.js`;
    const onGotAmdLoader = () => {

      if (window.LOADER_PENDING) {
        window.require.config(config);
      }

      // Load monaco
      window.require(['vs/editor/editor.main'], () => {
        callback();
      });

      // Call the delayed callbacks when AMD loader has been loaded
      if (window.LOADER_PENDING) {
        window.LOADER_PENDING = false;
        const loaderCallbacks = window.LOADER_CALLBACKS;
        if (loaderCallbacks && loaderCallbacks.length) {
          let currentCallback = loaderCallbacks.shift();
          while (currentCallback) {
            currentCallback.fn.call(currentCallback.window);
            currentCallback = loaderCallbacks.shift();
          }
        }
      }
    };

    // Load AMD loader if necessary
    if (window.LOADER_PENDING) {
      // We need to avoid loading multiple loader.js when there are multiple editors loading concurrently
      //  delay to call callbacks except the first one
      window.LOADER_CALLBACKS = window.LOADER_CALLBACKS || [];
      window.LOADER_CALLBACKS.push({
        window: this,
        fn: onGotAmdLoader
      });
    } else {
      if (typeof window.require === 'undefined') {
        const loaderScript = window.document.createElement('script');
        loaderScript.type = 'text/javascript';
        loaderScript.src = loaderUrl;
        loaderScript.addEventListener('load', onGotAmdLoader);
        window.document.body.appendChild(loaderScript);
        window.LOADER_PENDING = true;
      } else {
        onGotAmdLoader();
      }
    }
  }



Vue.component('monaco-editor',{
    template:`<div :style="style"></div>`,
    props: {
      width: { type: [String, Number], default: '100%' },
      height: { type: [String, Number], default: '100%' },
      code: { type: String, default: '// code \n' },
      srcPath: { type: String },
      language: { type: String, default: 'java' },
      theme: { type: String, default: 'vs' }, // vs, hc-black
      options: { type: Object, default: () => {} },
      highlighted: { type: Array, default: () => [{
        number: 0,
        class: ''
      }] },
      changethrottle: { type: Number}
    },
    mounted() {
      this.fetchEditor();
    },
    destroyed() {
      this.destroyMonaco();
    },
    computed: {
      style() {
        const { width, height } = this;
        const fixedWidth = width.toString().indexOf('%') !== -1 ? width : `${width}px`;
        const fixedHeight = height.toString().indexOf('%') !== -1 ? height : `${height}px`;
        return {
          width: fixedWidth,
          height: fixedHeight,
        };
      },
      editorOptions() {
        return Object.assign({}, this.defaults, this.options, {
          value: this.code,
          language: this.language,
          theme: this.theme
        });
      }
    },
    data() {
      return {
        defaults: {
          selectOnLineNumbers: true,
          roundedSelection: false,
          readOnly: false,
          cursorStyle: 'line',
          automaticLayout: false,
          glyphMargin: true
        }
      }
    },
    watch: {
      highlighted: {
        handler(lines) {
          this.highlightLines(lines);
        },
        deep: true
      }
    },
    methods: {
      highlightLines(lines) {
        if (!this.editor) {
          return;
        }
        lines.forEach((line) => {
          const className = line.class;
          const highlighted = this.$el.querySelector(`.${className}`);
  
          if (highlighted) {
            highlighted.classList.remove(className);
          }
  
          const number = parseInt(line.number);
          if (!this.editor && number < 1 || isNaN(number)) {
            return;
          }
  
          const selectedLine = this.$el.querySelector(`.view-lines [linenumber="${number}"]`);
          if (selectedLine) {
            selectedLine.classList.add(className);
          }
        });
      },
      editorHasLoaded(editor, monaco) {
        this.editor = editor;
        this.monaco = monaco;
        this.editor.onDidChangeModelContent(event =>
          this.codeChangeHandler(editor, event)
        );
        this.$emit('mounted', editor);
      },
      codeChangeHandler(editor) {
        //this.codeChange(editor);
        this.$emit('change', editor);
        /*
        if (this.codeChangeEmitter) {
          this.codeChangeEmitter(editor);
        } else {
          this.codeChangeEmitter = debounce(
            (editor)=> {
              this.$emit('change', editor);
            },
            this.changethrottle
          );
          this.codeChangeEmitter(editor);
        }*/
      },
      fetchEditor() {
        loadMonaco(this.srcPath, this.createMonaco);
      },
      createMonaco() {
        this.editor = window.monaco.editor.create(this.$el, this.editorOptions);
        this.editorHasLoaded(this.editor, window.monaco);
      },
      destroyMonaco() {
        if (typeof this.editor !== 'undefined') {
          this.editor.dispose();
        }
      }
    }
  });
