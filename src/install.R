#!/usr/bin/env Rscript 

# script for locally starting the primer design app for users
# -> installs the tool if necesary
######
# some code duplication here: need to find out the directory path ...
# -> not possible in another file, for which we would need again the path for sourcing
#######
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
message("\nNote: If the installation should stop with an error such as\n",
		"'Error in loadNamespace(): there is no package called `pkgName`', ", 
		"please install `pkgName` manually using `install.packages('pkgName')` for CRAN packages\n",
		"or `source('http://bioconductor.org/biocLite.R');biocLite('pkgName')` for ",
		"Bioconductor packages and continue the installation.\n")
Sys.sleep(1)
cmd.params <- commandArgs(trailingOnly=TRUE)
# arg1: logical (start.after.install?)
if (length(cmd.params) == 0) {
    start.after.install <- TRUE
    to.install.tools <- TRUE
} else {
    start.after.install <- as.logical(cmd.params[1])
    to.install.tools <- as.logical(cmd.params[2])
}
base.path <- file.path(path, "..") # the primer_design directory
path <- file.path(path, "openPrimeR") # path to openPrimeR package
#print("###")
#print("PATH")
#print(path)
#print("###")
source(file.path(path, "inst", "shiny", "shiny_server", "extra_install_helper.R"))
my_deps <- get_deps(path)
tool.data.folder <- file.path(path, "inst", "extdata")
# we need roxygen for devtools ...
# set CRAN mirror for 'available.packges' command:
CRAN.mirror <- set.CRAN.mirror("http://cran.uni-muenster.de/", "devtools", tool.data.folder = tool.data.folder)
CRAN.pkgs <- available.packages()
# install all openPrimeR R package dependencies
for (i in seq_along(my_deps)) {
	dep <- my_deps[i]
	# dependencies <- c("Depends", "Imports", "LinkingTo", "Suggests", "Enhances") # too many ...
	dependencies <- TRUE
	USED.MIRROR <- pkgTest(dep, load_namespace_only = TRUE, 
						   dependencies = dependencies,
						   repository = CRAN.mirror, CRAN.pkgs = CRAN.pkgs)
}
# Update packages that were available already but are outdated:
update.status <- update.required.packages(my_deps, CRAN.mirror)
# install openPrimeR from source:
install.ok <- try(install.packages(path, repos = NULL, type="source", dependencies = c("Depends", "Imports", "LinkingTo", "Suggests", "Enhances")), silent = TRUE)
if (class(install.ok) == "try-error") {
	# Installation stopped due to missing dependencies: let devtools do the job
	install.ok <- try(devtools::install(path, dependencies = TRUE, quick = TRUE, upgrade = FALSE))
	if (class(install.ok) == "try-error") {
		stop("Installation of openPrimeR failed. Please install missing dependencies manually and restart the installation procedure thereafter.")
	}
} else {
    #library("openPrimeR") # load the pkg, this is useless
}
if (to.install.tools) { # don't install tools for docker in one script call -> debug if there's an error
    # need to load current pkg version from source such that paths relate to the local install directory of the tool rather than system install dir
    print("Loading pkg from path:")
    print(path)
    # removing the variables doesn't help ...
    #rm(list = setdiff(ls(), "path")) # remove everything from environment to prevent error
    load.ok <- try(devtools::load_all(path, export_all = FALSE), silent = FALSE)
    if (class(load.ok) == "try-error") {
        # try to continue here: docker complains for some reason at the moment ..
        warning("Could not load openPrimeR using devtools: ",
             attr(load.ok, "condition"))
        SHINY.PATH <- file.path(path, "inst", "shiny") # set shiny path for install_tools script necessary now
    } else {
        SHINY.PATH <- system.file("shiny", package = "openPrimeR")
    }
    install.script <- file.path(SHINY.PATH, "shiny_server", "extra_install_tools.R")
    if (install.script == "") {
        stop("There was an error during the installation procedure. ",
            "Install script could not be located, probably since 'devtools' couldn't load the package.")
    }
    source(install.script)
    # start the shiny app:
    # add icon:
    icon.status <- create_tool_icon(base.path)
    if (start.after.install) {
        message("Succesfully installed openPrimeR! Starting the app ...")
        openPrimeR::startApp()
    } else {
        message("Succesfully installed openPrimeR!")
    }
}
