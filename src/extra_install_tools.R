###########
# Checks and installs all required stand-alone tools
##########
# important: if we don't use devtools, then the path MAY be set to the R library path instead of the local path -> tools/ won't be found!
if (!exists("base.path")) {
    stop("Base path wasn't set before calling install.tools()")
}
source(file.path(base.path, "src", "extra_install_helper.R")) # install functions
if (exists("AVAILABLE.TOOLS") && length(AVAILABLE.TOOLS) != 0) {
    AVAILABLE.TOOLS <- install.tools(AVAILABLE.TOOLS)
} else {
    AVAILABLE.TOOLS <- install.tools()
}

