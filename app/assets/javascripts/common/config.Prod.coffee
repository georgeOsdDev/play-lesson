console.log "This is config.coffee"
((window)->
  App = window.App || {}
  App.config =
    "env":"develop"
  window.App = app
)(window)
