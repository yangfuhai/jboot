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
      {text: 'Jboot文档', link: '/docs/'},
      {text: 'JbootAdmin', link: '/jbootadmin/'},
      {text: '提问', link: 'https://gitee.com/JbootProjects/jboot/issues'},
      {text: 'JPress', link: 'http://www.jpress.io'},
      {
        text: '源码下载', items: [
          {text: 'Gitee', link: 'https://gitee.com/JbootProjects/jboot'},
          {text: 'Github', link: 'https://github.com/yangfuhai/jboot'}
        ]
      },
    ],

    sidebar: {
      '/docs/': [{
        title: '认识 Jboot',
        collapsable: false,
        children: [
          {title: 'Jboot 简介', path: '/docs/'},
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
            {title: 'Validator', path: '/docs/validator'},
            {title: 'WebSocket', path: '/docs/websocket'},
            {title: 'Json', path: '/docs/json'},
            {title: 'Jwt', path: '/docs/jwt'},
            {title: 'Swagger', path: '/docs/swagger'},
            {title: 'AOP', path: '/docs/aop'},
            {title: '数据库', path: '/docs/db'},
            {title: '缓存', path: '/docs/cache'},
            {title: 'Redis', path: '/docs/redis'},
            {title: 'RPC 调用', path: '/docs/rpc'},
            {title: 'MQ 消息队列', path: '/docs/mq'},
            {title: 'Gateway 网关', path: '/docs/gateway'},
            {title: '任务调度', path: '/docs/schedule'},
            {title: 'Jboot限流', path: '/docs/limit'},
            {title: 'Sentinel限流', path: '/docs/sentinel'},
            {title: '分布式附件管理', path: '/docs/attachment'},
            {title: '监控', path: '/docs/metrics'},
            {title: '事件机制', path: '/docs/event'},
            {title: '序列化', path: '/docs/serialize'},
            {title: 'SPI扩展', path: '/docs/spi'},
            {title: '单元测试', path: '/docs/junit'},
            {title: '代码生成器', path: '/docs/codegen'},
            {title: 'API 文档生成', path: '/docs/apidoc'},
            {title: '项目构建', path: '/docs/build'},
            {title: '项目部署', path: '/docs/deploy'},
            {title: 'Docker', path: '/docs/docker'},
            {title: '热加载', path: '/docs/hotload'},
            {title: 'Swagger', path: '/docs/swagger'},
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


      '/jbootadmin/': [{
        title: '认识 JbootAdmin',
        collapsable: false,
        children: [
          {title: '简介', path: '/jbootadmin/'},
          {title: '功能介绍', path: '/jbootadmin/feature'},
          {title: '我要购买', path: '/jbootadmin/buy'}
        ],
      },
        {
          title: '开发文档',
          collapsable: false,
          children: [
            {title: '开始', path: '/jbootadmin/start'},
            {title: '数据库设计', path: '/jbootadmin/db'},
            {title: '后台菜单', path: '/jbootadmin/menu'},
            {title: '权限设计', path: '/jbootadmin/permission'},
            {title: '前端组件', path: '/jbootadmin/front'},
            {title: '安全防护', path: '/jbootadmin/safety_precautions'},
          ],
        },
        {
          title: '运维和部署',
          collapsable: false,
          children: [
            {title: '部署', path: '/jbootadmin/deploy'},
            {title: 'CDN配置', path: '/jbootadmin/cdn'},
            {title: '文件同步', path: '/jbootadmin/attachment'},
            {title: '配置中心', path: '/jbootadmin/config'},
            {title: '门户网关', path: '/jbootadmin/gateway'},
            {title: '服务器管理', path: '/jbootadmin/server'},
          ],
        },
      ]
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
