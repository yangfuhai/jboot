//参考：
// https://github.com/vuejs/vuepress/blob/master/packages/docs/docs/.vuepress/config.js
// https://vuepress-theme-reco.recoluan.com/views/1.x/
module.exports = {
  title: 'Jboot 官方网站',
  description: 'Jboot 一个开源的分布式、商业级微服务框架。',
  // base:'/docs/',

  theme: 'vuepress-theme-reco',
  themeConfig: {
    //腾讯 404 公益配置
    noFoundPageByTencent: false,

    mode: 'light', // 默认 auto，auto 跟随系统，dark 暗色模式，light 亮色模式
    modePicker: false, // 默认 true，false 不显示模式调节按钮，true 则显示

    // author
    author: 'jboot',

    // if your docs are in a different repo from your main project:
    docsRepo: 'yangfuhai/jboot',
    // if your docs are in a specific branch (defaults to 'master'):
    docsBranch: 'master',
    // if your docs are not at the root of the repo:
    docsDir: 'doc',
    // defaults to false, set to true to enable
    editLinks: true,
    // custom text for edit link. Defaults to "Edit this page"
    editLinkText: '编辑此页面',


    lastUpdated: '更新时间', // string | boolean

    nav: [
      {text: '首页', link: '/'},
      {text: '提问', link: 'https://gitee.com/JPressProjects/jpress/issues'},
      {text: 'JPress', link: 'http://www.jpress.io'},
      {
        text: '源码下载', items: [
          {text: 'Gitee', link: 'https://gitee.com/JbootProjects/jboot'},
          {text: 'Github', link: 'https://github.com/yangfuhai/jboot'}
        ]
      },
    ],

    sidebar: {
      '/': [{
        title: '认识 Jboot',
        collapsable: false,
        children: [
          {title: 'Jboot 简介', path: '/'},
          {title: '快速开始', path: '/docs/start'}
        ],
      },

        {
          title: '开发文档',
          collapsable: false,
          children: [
            {title: '安装', path: '/docs/install'},
            {title: '配置', path: '/docs/config'},
            {title: 'JFinalConfig', path: '/docs/jfinalConfig'},
            {title: 'MVC', path: '/docs/mvc'},
            {title: 'WebSocket', path: '/docs/websocket'},
            {title: 'Jwt', path: '/docs/jwt'},
            {title: 'Swagger', path: '/docs/swagger'},
            {title: 'Aop', path: '/docs/aop'},
            {title: '数据库', path: '/docs/db'},
            {title: '缓存', path: '/docs/cache'},
            {title: 'Redis', path: '/docs/redis'},
            {title: 'RPC 调用', path: '/docs/rpc'},
            {title: 'MQ 消息队列', path: '/docs/mq'},
            {title: 'Gateway 网关', path: '/docs/gateway'},
            {title: '任务调度', path: '/docs/schedule'},
            {title: '限流', path: '/docs/limit'},
            {title: '监控', path: '/docs/metrics'},
            {title: '事件机制', path: '/docs/event'},
            {title: '序列化', path: '/docs/serialize'},
            {title: 'SPI扩展', path: '/docs/spi'},
            {title: '代码生成器', path: '/docs/codegen'},
            {title: '项目构建', path: '/docs/build'},
            {title: '项目部署', path: '/docs/deploy'},
            {title: 'Docker', path: '/docs/docker'},
          ],
        },

        {
          title: '性能',
          collapsable: false,
          children: [
            {title: '性能测试', path: '/docs/benchmark'},
          ],
        }
      ],
    },
    sidebarDepth: 1
  },

  head: [
    ['link', {rel: 'icon', href: '/logo.png'}],
    ['script', {}, `
            var _hmt = _hmt || [];
            (function() {
              var hm = document.createElement("script");
              hm.src = "https://hm.baidu.com/hm.js?d6b9b94a6fafaa41c63920e1af80bcaf";
              var s = document.getElementsByTagName("script")[0]; 
              s.parentNode.insertBefore(hm, s);
            })();
        `]
  ]
}
