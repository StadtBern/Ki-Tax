/*jslint node: true */
'use strict';

var pkg = require('./package.json');
var globs = require('./globbing-util.js');

module.exports = function (grunt) {

    // load all grunt tasks
    require('load-grunt-tasks')(grunt);

    // Project configuration.
    grunt.initConfig({
        connect: {
            options: {
                port: 9001,
                // hostname: 'localhost',
                hostname: '*', // enable to allow remote access to this server
                middleware: function (connect, options, middlewares) {
                    var proxy = require('grunt-connect-proxy/lib/utils').proxyRequest;
                    return [proxy].concat(middlewares);
                }
            },
            dev: {
                proxies: [
                    {
                        context: '/ebegu',
                        host: 'localhost',
                        port: 8080,
                        https: false,
                        xforward: false,
                        headers: {
                            'Connection': 'close'
                        }
                    }
                ]
            },
            swaggerUi: {
                options: {
                    port: 9002,
                    // hostname: 'localhost',
                    base: './dist/swagger-ui',
                    host: 'localhost',
                    open: 'http://localhost:9002/index.html?url=http://localhost:8080/ebegu/api/v1/swagger.json#!/default/'
                    }

            },

        },
        watch: {
            options: {
                spawn: false
            },
            main: {
                options: {
                    livereload: true,
                    livereloadOnError: false
                },
                files: [globs.createFolderGlobs(['*.js', '*.ts', '*.html']), '!_SpecRunner.html', '!.grunt'],
                tasks: [] //all the tasks are run dynamically during the watch event handler
            },
            less: {
                files: [globs.createFolderGlobs(['*.less'])],
                tasks: ['less:development'],
                options: {
                    spawn: true
                }
            },
            css: {
                files: ['temp/app.css'],
                options: {
                    livereload: true
                }
            }
        },
        jshint: {
            options: {
                jshintrc: '.jshintrc'
            },
            main: {
                src: 'src/**/*.js'
            },
            jenkins: {
                reporter: 'checkstyle',
                reporterOutput: 'build/checkstyle-result.xml',
                src: 'src/**/*.js'
            }
        },
        jscs: {
            options: {
                config: '.jscsrc',
                verbose: true // If you need output with rule names http://jscs.info/overview.html#verbose
            },
            main: {
                src: 'src/**/*.js'
            },
            jenkins: {
                reporter: 'junit',
                reporterOutput: 'build/jscs-results.xml',
                src: 'src/**/*.js'
            }
        },
        clean: {
            before: {
                src: ['dist', 'temp', 'build']
            },
            after: {
                src: ['temp']
            }
        },
        less: {
            production: {
                options: {},
                files: {
                    'temp/app.css': 'src/app.module.less'
                }
            },
            development: {
                options: {
                    rootpath: '../' // because app.css is served from a nested folder
                },
                files: {
                    'temp/app.css': 'src/app.module.less' // destination file and source file
                }
            }
        },
        ngtemplates: {
            main: {
                options: {
                    module: pkg.name,
                    htmlmin: '<%= htmlmin.main.options %>'
                },
                src: [globs.createFolderGlobs('*.html'), '!index.html', '!_SpecRunner.html'],
                dest: 'temp/templates.js'
            }
        },
        copy: {
            main: {
                files: [
                    {src: ['node_modules/font-awesome/fonts/**'], dest: 'dist/', filter: 'isFile', expand: true},
                    {src: ['node_modules/bootstrap/fonts/**'], dest: 'dist/', filter: 'isFile', expand: true},
                    {src: ['src/translations/**'], dest: 'dist/', filter: 'isFile', expand: true},
                    {src: ['package.json'], dest: 'dist/', filter: 'isFile', expand: true},
                    {src: ['src/images/**'], dest: 'dist/'},
                    //vielleicht kann man hier swagger-ui separat deployen todo team remove
                    {cwd: 'node_modules/swagger-ui/dist/', src: ['**'], dest: 'dist/swagger-ui/', expand: true},
                    // {src: ['node_modules/swagger-ui/dist/*'], dest: 'dist/swagger-ui/'},
                    //todo bilder  einfuegen {src: ['node_modules/bootstrap/fonts/**'], dest: 'dist/', filter: 'isFile', expand: true}
                ]
            }
        },
        dom_munger: { // jshint ignore:line
            read: {
                options: {
                    read: [
                        {selector: 'script[data-concat!="false"]', attribute: 'src', writeto: 'appjs'},
                        {selector: 'link[rel="stylesheet"][data-concat!="false"]', attribute: 'href', writeto: 'appcss'}
                    ]
                },
                src: 'index.html'
            },
            update: {
                options: {
                    remove: ['script[data-remove!="false"]', 'link[data-remove!="false"]'],
                    append: [
                        {selector: 'body', html: '<script src="app.full.min.js"></script>'},
                        {selector: 'head', html: '<link rel="stylesheet" href="app.full.min.css">'}
                    ]
                },
                src: 'index.html',
                dest: 'dist/index.html'
            }
        },
        cssmin: {
            main: {
                src: ['temp/app.css', '<%= dom_munger.data.appcss %>'],
                dest: 'dist/app.full.min.css'
            }
        },
        concat: {
            main: {
                src: ['<%= dom_munger.data.appjs %>', '<%= ngtemplates.main.dest %>'],
                dest: 'temp/app.full.js'
            }
        },
        ngAnnotate: {
            main: {
                src: 'temp/app.full.js',
                dest: 'temp/app.full.js'
            }
        },
        uglify: {
            main: {
                src: 'temp/app.full.js',
                dest: 'dist/app.full.min.js'
            }
        },
        htmlmin: {
            main: {
                options: {
                    collapseBooleanAttributes: true,
                    collapseWhitespace: true,
                    removeAttributeQuotes: true,
                    removeComments: true,
                    removeEmptyAttributes: true,
                    removeScriptTypeAttributes: true,
                    removeStyleLinkTypeAttributes: true
                },
                files: {
                    'dist/index.html': 'dist/index.html'
                }
            }
        },
        //Imagemin has issues on Windows.
        //To enable imagemin:
        // - "npm install grunt-contrib-imagemin"
        // - Comment in this section
        // - Add the "imagemin" task after the "htmlmin" task in the build task alias
        // imagemin: {
        //   main:{
        //     files: [{
        //       expand: true, cwd:'dist/',
        //       src:['**/{*.png,*.jpg}'],
        //       dest: 'dist/'
        //     }]
        //   }
        // },
        karma: {
            options: {
                frameworks: ['jasmine'],
                files: [  //this files data is also updated in the watch handler, if updated change there too
                    '<%= dom_munger.data.appjs %>', // TODO flatten dom_munger.data.appjs for Karma Versions > 0.8.*
                    'node_modules/angular-mocks/angular-mocks.js',
                    globs.createFolderGlobs('*.spec.js')
                ],
                logLevel: 'ERROR',
                reporters: ['mocha'],
                autoWatch: false, //watching is handled by grunt-contrib-watch
                singleRun: true,
                preprocessors: {
                    'src/**/*.html': 'ng-html2js'
                },
                colors: true
            },
            all_tests: { // jshint ignore:line
                browsers: ['PhantomJS', 'Chrome']
                //browsers: ['PhantomJS', 'Chrome', 'Firefox']
            },
            during_watch: {
                browsers: ['PhantomJS']
            },
            jenkins: {
                browsers: ['PhantomJS'],
                reporters: ['mocha', 'junit', 'coverage'],
                preprocessors: {
                    'src/**/*.html': 'ng-html2js',
                    'src/**/*': ['coverage']
                },
                junitReporter: {
                    outputFile: 'build/karma-results.xml',
                    useBrowserName: false
                },
                coverageReporter: {
                    type: 'cobertura',
                    dir: 'build/coverage',
                    subdir: '.'
                },
                client: {
                    useIframe: false //to avoid phantomjs detect failure
                }
            }
        },
        jsdoc: {
            dist: {
                src: 'src/**/*.js',
                options: {
                    destination: 'build/doc'
                }
            }
        },
        ts: {
            default: {
                tsconfig: {
                    updateFiles: false
                }
            }
        }
    });

    grunt.registerTask('build', ['clean:before', 'jshint:main', 'jscs:main', 'less:production', 'dom_munger', 'ngtemplates', 'cssmin', 'concat', 'ngAnnotate', 'uglify', 'copy', 'htmlmin', 'clean:after']);
    grunt.registerTask('serve', ['dom_munger:read', 'jshint:main', 'jscs:main', 'configureProxies:dev', 'connect:dev', 'ts', 'less:development', 'watch']);
    grunt.registerTask('serve-swagger', ['build','connect:swaggerUi:keepalive']);
    grunt.registerTask('test', ['dom_munger:read', 'karma:all_tests']);
    grunt.registerTask('doc', ['jsdoc']);
    grunt.registerTask('type', ['ts']);
    grunt.registerTask('jenkins-build', ['clean:before', 'jshint:jenkins', 'jscs:jenkins', 'less:production', 'dom_munger:read', 'karma:jenkins', 'dom_munger', 'ngtemplates', 'cssmin', 'concat', 'ngAnnotate', 'uglify', 'copy', 'htmlmin', 'clean:after']);

    grunt.event.on('watch', function (action, filepath) {
        //https://github.com/gruntjs/grunt-contrib-watch/issues/156

        var tasksToRun = [];

        if (filepath.lastIndexOf('.js') !== -1 && filepath.lastIndexOf('.js') === filepath.length - 3) {

            //lint the changed js file
            grunt.config('jshint.main.src', filepath);
            tasksToRun.push('jshint');
            grunt.config('jscs.main.src', filepath);
            tasksToRun.push('jscs:main');

            //find the appropriate unit test for the changed file
            var spec = filepath;
            if (filepath.lastIndexOf('.spec.js') === -1 || filepath.lastIndexOf('.spec.js') !== filepath.length - 8) {
                spec = filepath.substring(0, filepath.length - 3) + '.spec.js';
            }

            //if the spec exists then lets run it
            if (grunt.file.exists(spec)) {
                var files = [].concat(grunt.config('dom_munger.data.appjs'));
                files.push('node_modules/angular-mocks/angular-mocks.js');
                files.push(spec);
                grunt.config('karma.options.files', files);
                tasksToRun.push('karma:during_watch');
            }
        }

        //if index.html changed, we need to reread the <script> tags so our next run of karma
        //will have the correct environment
        if (filepath === 'index.html') {
            tasksToRun.push('dom_munger:read');
        }

        grunt.config('watch.main.tasks', tasksToRun);

    });
};
