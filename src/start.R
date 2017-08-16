#!/usr/bin/env Rscript 

# script for locally starting the primer design app for users
# -> installs the tool if necesary
######
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
library(methods) # for rscript calls
#########
# set working directory to directory of openPrimer package
path <- try(getScriptPath(), silent=TRUE) # for Rscript call of start.R
if (class(path) == "try-error") { # for non-Rscript calls
    path <- try(dirname(this.file.name()), silent = TRUE)
}
if (class(path) == "try-error") { # other cases
    path <- getwd() # assume we're in the right directory ('src')
} 
path <- file.path(path, "openPrimeR") # path to openPrimeR package
source(file.path(path, "inst", "shiny", "shiny_server", "extra_install_helper.R"))
# make sure that openPrimeR has been installed.
if (!requireNamespace("openPrimeR", quietly = TRUE)) { # don' attach here!!
    warning("Could not load the openPrimeR package. ",
	"Please run the install script first to install openPrimeR if you haven't done so yet. ",
	"Trying to fix the problem now, assuming you already ran the installation script ...")
}
# load current pkg version from source (no library call here such that changes to the source are directly reflected in the tool).
test <- suppressWarnings(try(devtools::load_all(path, export_all = FALSE), silent = TRUE))
if (class(test) == "try-error") {
    print(attr(test, "condition"))
    message("openPrimeR is installed, but couldn't be loaded by devtools.\n",
            "Maybe the package sources are invalid?\n",
            "Have you tried updating your sources?\n", 
            "Meanwhile, trying to reinstall all dependencies to see if this helps to fix the problem ...")
    Sys.sleep(2)
	# tool is installed but doesn't load -> dependencies might be missing ...
	source(file.path(path, "inst", "shiny", "shiny_server", "extra_install_helper.R"))
	tool.data.folder <- file.path(path, "inst", "extdata")
	# set CRAN mirror for 'available.packges' command:
	CRAN.mirror <- set.CRAN.mirror("http://cran.uni-muenster.de/", "devtools", tool.data.folder = tool.data.folder)
	CRAN.pkgs <- available.packages()
	required.pkgs <- get_deps(path)
	#installed.pkgs <- installed.packages()
	#missing.pkgs <- intersect(required.pkgs, installed.pkgs[, "Package"])
	for (i in seq_along(required.pkgs)) {
		dep <- required.pkgs[i]
		message("######")
		message("Installing: ", dep)
		message("######")
		# use force_install to update all re-install/update all dependencies
		USED.MIRROR <- pkgTest(dep, load_namespace_only = TRUE, 
						   dependencies = TRUE,
						   repository = CRAN.mirror, CRAN.pkgs = CRAN.pkgs, force_install = TRUE)
	}
	# try again to load the package
	message("Reloading openPrimeR ...")
	test <- try(devtools::load_all(path, export_all = FALSE))
	if (class(test) == "try-error") {
		stop("Could not load openPrimeR. Please try reinstalling the tool.")
	} else {
		message("Successfully updated the dependencies!")
	}
}
# don't install tools, some users might be happy with what they have or have problems with installing ...
message("Starting the app ...")
# start the shiny app:
openPrimeR::startApp()

#####################################3
###################
# useful comands:
####################
# options(shiny.trace = TRUE)
# BIOCLITE INSTALL
# source("https://bioconductor.org/biocLite.R")
# biocLite(lib = "/local/home/mdoering/R/x86_64-pc-linux-gnu-library/3.3", lib.loc = "/local/home/mdoering/R/x86_64-pc-linux-gnu-library/3.3")
# 
# devtools::load_all()
# devtools::document()
# devtools::check() # automatically builds vignettes
# R CMD check --no-build-vignettes --no-manual openPrimeR
# devtools::run_examples() # only run examples
# devtools::test() # do all unit tests
# R CMD Rd2pdf src/openPrimeR # create pkg manual with all functions in man/ folder
# devtools::build_vignettes()
# BiocCheck("./") # BioConductor additional checks
# render rmarkdown html:
# rmarkdown::render("src/openPrimeR/vignettes/openPrimeR_vignette.Rmd")
# rmarkdown::render("src/openPrimeR/inst/tutorials/introduction/introduction.Rmd")

# learnr::run_tutorial("introduction", package = "openPrimeR")
####
# RELEASE TESTS
######
# R CMD check
# R CMD build
# R CMD BiocCheck <pkg>
