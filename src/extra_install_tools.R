# Checks and installs all required stand-alone tools
#if (!exists("SHINY.PATH")) {
    #SHINY.PATH <- system.file("shiny", package = "openPrimeRui")
#}
#source("extra_set_paths.R")) # load paths for tool installation
if (!exists("base.path")) {
    stop("Base path wasn't set before calling install.tools()")
}
source(file.path(base.path, "src", "extra_install_helper.R")) # install functions
if (exists("AVAILABLE.TOOLS") && length(AVAILABLE.TOOLS) != 0) {
    AVAILABLE.TOOLS <- install.tools(AVAILABLE.TOOLS)
} else {
    AVAILABLE.TOOLS <- install.tools()
}

