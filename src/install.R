#!/usr/bin/env Rscript 
###
# installs openPrimeR and openPrimeRui, as well as additional tools
#############
if (!require("devtools")) {
    return("Please install devtools")
}
devtools::install("src/openPrimeR", dependencies = TRUE)
devtools::install("src/openPrimeRui", dependencies = TRUE)
getScriptPath <- function() {
    cmd.args <- commandArgs()
    m <- regexpr("(?<=^--file=).+", cmd.args, perl = TRUE)
    script.dir <- dirname(regmatches(cmd.args, m))
    if (length(script.dir) == 0) 
        stop("can't determine script dir: please call the script with Rscript")
    if (length(script.dir) > 1) 
        stop("can't determine script dir: more than one '--file' argument detected")
    # if spaces are in script dir, they are replaced with '~+~'
    script.dir <- gsub("~\\+~", " ", script.dir)
    return(script.dir)
}
this.file.name <- function() {
    frame_files <- lapply(sys.frames(), function(x) x$ofile)
    frame_files <- Filter(Negate(is.null), frame_files)
    frame_files[[length(frame_files)]]
}

# set working directory to directory of openPrimer package
path <- try(getScriptPath(), silent=TRUE) # for Rscript call of start.R
if (class(path) == "try-error") { # for non-Rscript calls
    path <- try(dirname(this.file.name()), silent = TRUE)
}
if (class(path) == "try-error") { # other cases
    path <- getwd() # assume we're in the right directory ('src')
}
# check for whitespace in string
dir.disallowed <- grepl("[[:blank:]]", path)
if (dir.disallowed) {
	stop("Please move the base directory to a path without any spaces.")
}
cmd.params <- commandArgs(trailingOnly=TRUE)
# arg1: logical (start.after.install?)
if (length(cmd.params) == 0) {
    start.after.install <- TRUE
    to.install.tools <- TRUE
} else {
    start.after.install <- as.logical(cmd.params[1])
    to.install.tools <- as.logical(cmd.params[2])
}
base.path <- file.path(path, "..") # the primer_design directory (base)
source(file.path(base.path, "src", "extra_install_helper.R"))

if (to.install.tools) {
    # installt tools
    install.script <- file.path(base.path, "src", "extra_install_tools.R")
    if (install.script == "") {
        stop("There was an error during the installation procedure. ",
            "Install script could not be located!")
    }
    source(install.script)
    # add icon:
    icon.status <- create_tool_icon(base.path)
    if (start.after.install) {
        # start the shiny app:
        message("Succesfully installed openPrimeR! Starting the app ...")
        openPrimeRui::startApp()
    } else {
        message("Succesfully installed openPrimeR!")
    }
}
