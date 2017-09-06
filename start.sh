#!/usr/bin/env bash
# start script for the R version of openPrimeR
# check whether R is available
if [[ $(command -v Rscript 2>/dev/null) ]]; then
    echo 'Using R at: ' $(command -v R)
else
    echo "It seems that R is not available on your system. Please install R first."
    exit
fi
dir=$(cd -P -- "$(dirname -- "$0")" && pwd -P);
export PATH=$PATH:"$dir/tools/oligoarrayaux/bin/:$dir/tools/MAFFT/bin/:$dir/tools/ViennaRNA/bin/:$dir/tools/MELTING/executable/:$dir/tools/phantomjs/bin/:$dir/tools/Pandoc/";
export UNAFOLDDAT="$dir/tools/oligoarrayaux/share/oligoarrayaux/";
env  LC_CTYPE="${LC_ALL:-${LC_CTYPE:-$LANG}}" PATH="$PATH" USER="$USER" UNAFOLDDAT="$UNAFOLDDAT" PWD="$dir" "$dir/src/start.R";
