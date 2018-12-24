#############
# Analyses for the primer amplification manuscript
###############
# Please see the openPrimeR code in "data-raw/RefCoverage.R" at github.com/matdoering/openPrimeR/ for the code that generated the feature matrix ("feature.matrix")
###############
# Load data and functions
###########
data(Ippolito) # IGHV templates
source("load_functions.R") # functions for doing the analyses
devtools::load_all(file.path("..", "openPrimeR")) # load all functions from openPrimeR
data(RefCoverage) # data with amplification status ('feature.matrix')
library(MASS) # for stepAIC
library(caret)
library(plyr)
library(ROCR) # for roc analysis
cur.out.folder <- file.path("results")
dir.create(cur.out.folder)
# rename feature.matrix Runs:
feature.matrix$Run[feature.matrix$Run == "openPrimeR2017"] <- "Set1"
feature.matrix$Run[feature.matrix$Run == "Tiller2008_1st"] <- "Set2"
##########
# Statistical Analysis of Data set
###########
# Create scatter plot of DeltaG vs terminal mismatch position
plot_scatter_deltaG_pos(feature.matrix, cur.out.folder)
plot_rate_of_amplification(feature.matrix, cur.out.folder)
# Create table providing data set overview
data.overview <- ddply(feature.matrix, "Run", summarize, DeltaGRange = iqr(annealing_DeltaG),
    MismatchRange = iqr(Position_3terminusLocal), NbrMismatchesRange = iqr(Number_of_mismatches_hexamer), GC_Clamp = iqr(gc_clamp_fw), Folding = iqr(Structure_deltaG_fw), SelfDimer = iqr(Self_Dimer_DeltaG), AmplifiedCount = length(which(Experimental_Coverage == "Amplified")), 
    SetSize = length(Experimental_Coverage), MM_1 = length(which(Mismatch_pos_1 == 1)), 
    MM_2 = length(which(Mismatch_pos_2 == 1)),
    MM_3 = length(which(Mismatch_pos_3 == 1)),
    MM_4 = length(which(Mismatch_pos_4 == 1)),
    MM_5 = length(which(Mismatch_pos_5 == 1)),
    MM_6 = length(which(Mismatch_pos_6 == 1)),
    AvgMMHexamer = mean(Number_of_mismatches_hexamer))
write.csv(data.overview, file.path(cur.out.folder, "data_overview.csv")) # openPrimeR vs Tiller properties
#######
# Test the association between Delta G and i_x regarding amplification status
#####
# Define indices for amplified / unamplified primer-template pairs
amp.idx <- which(feature.matrix$Experimental_Coverage == "Amplified") # amplified observations
namp.idx <- which(feature.matrix$Experimental_Coverage == "Unamplified") # unamplified observations
# association between coverage status and free energy of annealing:
test.result.G <- wilcox.test(feature.matrix$annealing_DeltaG[amp.idx], feature.matrix$annealing_DeltaG[namp.idx], paired = FALSE)
# association of mismatches with the outcome
test.result.mm <- wilcox.test(feature.matrix$Position_3terminusLocal[amp.idx], feature.matrix$Position_3terminusLocal[namp.idx], paired = FALSE)
wilcox.result <- data.frame("Test" = c("DeltaG", "Mismatch"), "p_value" = c(test.result.G$p.value, test.result.mm$p.value))
write.csv(wilcox.result, file.path(cur.out.folder, "result_wilcox_test.csv"))
########
# Gather basic statistics for differences between amplified/unamplified samples
#######
# DeltaG
iqr.DeltaG.amp <- iqr(feature.matrix$annealing_DeltaG[amp.idx])
iqr.DeltaG.namp <- iqr(feature.matrix$annealing_DeltaG[namp.idx])
# Mismatches Hexamer
iqr.mm.amp <- iqr(feature.matrix[amp.idx, "Number_of_mismatches_hexamer"])
iqr.mm.namp <- iqr(feature.matrix[namp.idx, "Number_of_mismatches_hexamer"])
# Position of worst-case 3' hexamer mismatch
iqr.pos.amp <- iqr(feature.matrix[amp.idx, "Position_3terminusLocal"])
iqr.pos.namp <- iqr(feature.matrix[namp.idx, "Position_3terminusLocal"])
basic.stats <- data.frame("Feature" = c("DeltaG", "NumberMismatchesHexamer", "PositionTerminalMismatchHexamer"), 
                          "IQR_Amplified" = c(iqr.DeltaG.amp, iqr.mm.amp, iqr.pos.amp),
                          "IQR_Unamplified" = c(iqr.DeltaG.namp, iqr.mm.namp, iqr.pos.namp))
write.csv(basic.stats, file.path(cur.out.folder, "stats_amplified_vs_unamplified.csv"))
# Plot heatmap of amplification status
# Heatmap for experimental data
plot.heatmap.exp(feature.matrix, cur.out.folder)
# Heatmap for predicted amplification status using TMM
tiller.df <- primer.data[[1]]$Primers # from data(RefCoverage)
open.df <- primer.data[[2]]$Primers # from data(RefCoverage)
primer.df <- rbind(data.frame(open.df), data.frame(tiller.df))
sel <- which(template.df$ID %in% feature.matrix$Template)
plot.heatmap.pred(template.df, primer.df, sel, cur.out.folder)
########
# Primer properties overview
sel.features <- c("Primer", "gc_ratio_fw", "primer_length_fw", "gc_clamp_fw", "Self_Dimer_DeltaG", "Structure_deltaG_fw")
primer.ids <- primer.df$ID
my.primers <- feature.matrix[match(primer.df$ID, feature.matrix$Primer),sel.features]
my.primers$gc_ratio <- paste0(round(my.primers$gc_ratio * 100, 1), "%")
my.primers$Self_Dimer_DeltaG <- round(my.primers$Self_Dimer_DeltaG, 1)
my.primers$Structure_deltaG_fw <- round(my.primers$Structure_deltaG_fw, 1)
write.csv(my.primers, file.path(cur.out.folder, "primer_properties.csv"))
# Investigate terminal dinucleotides
dinuc <- substr(primer.df$Forward, nchar(primer.df$Forward) - 1, nchar(primer.df$Forward))
dinucleotide.table <- as.data.frame(table(dinuc))
write.csv(dinucleotide.table, file.path(cur.out.folder, "primer_terminal_dinucleotides.csv"))
#######
# Logistic regression models
#########
# Create formulas for LR1 (stat.formula.1) and LR2 (stat.formula.2)
stat.formula.1 <- as.formula("Experimental_Coverage ~ 
                Mismatch_pos_1 + Mismatch_pos_2 + Mismatch_pos_3 + 
                Mismatch_pos_4 + Mismatch_pos_5 + Mismatch_pos_6 +
                Number_of_mismatches_hexamer + 
                annealing_DeltaG")
stat.formula.2 <- as.formula("Experimental_Coverage ~ 
                Mismatch_pos_1 + Mismatch_pos_2 + Mismatch_pos_3 + 
                Mismatch_pos_4 + Mismatch_pos_5 + Mismatch_pos_6 +
                Number_of_mismatches_hexamer + 
                annealing_DeltaG +
                Position_3terminusLocal +
                annealing_DeltaG * Position_3terminusLocal")
formulas <- list(stat.formula.1, stat.formula.2)
# For each formula, create a logistic regression model (LR1, LR2):
for (i in seq_along(formulas)) {
    stat.formula <- formulas[[i]]
    stat.model <- train(stat.formula, data = feature.matrix, method = "glm", family="binomial")
    nbr.features <- length(stat.model$trainingData)
    p.cut <- 0.05 / nbr.features # adjust p-value cutoff
    stat.summary <- summary(stat.model)
    p.vals <- stat.summary$coefficients[, "Pr(>|z|)"]
    # show significant p values
    print(paste("LR", i, ":", "p_cut:", p.cut, "; significant features: "))
    print(paste(names(p.vals)[which(p.vals < p.cut)], collapse = ", "))
    sink(file.path(cur.out.folder, paste0("feature_significance_logistic_", i, ".txt")))
    print(summary(stat.model))
    sink(NULL)
}
#########
# Define validation, training, and test data sets
#########
seed <- 164 # fixed seed for reproducibility 
set.indices <- get_train_indices_new(feature.matrix, seed = seed)
validation.matrix <- feature.matrix[set.indices$validation,]
validation.model <- glm(stat.formula.2, family = "binomial", data = validation.matrix)
train.matrix <- feature.matrix[set.indices$train,]
test.matrix <- feature.matrix[set.indices$test, ]
# Create table with number of observations per data set:

set.distribution <- data.frame(N = c(nrow(feature.matrix), nrow(validation.matrix), nrow(train.matrix), nrow(test.matrix)), 
           N_Amplified = c(length(amp.idx), length(intersect(amp.idx, set.indices$validation)), 
                        length(intersect(amp.idx, set.indices$train)),
                        length(intersect(amp.idx, set.indices$test))),
           N_Unamplified = c(length(namp.idx), length(intersect(namp.idx, set.indices$validation)), 
                          length(intersect(namp.idx, set.indices$train)),
                            length(intersect(namp.idx, set.indices$test))))
set.dist.ratios <- apply(set.distribution, 2, function(x) x/set.distribution[,1])
percentages <- apply(set.dist.ratios, 2, function(x) paste0(round(100 * x, 2), "%"))
set.dist.table <- cbind(set.distribution, percentages)
write.csv(set.dist.table, file.path(cur.out.folder, "data_set_distribution.csv"))
########
# Model development for TMM
#########
# a) Select features for logistic regression models using the validation set and backward selection from full set of LR2 features
# UPDATE: 2018-12-24: use of leaps rather than stepAIC (exact subset selection)
library(bestglm)
sink(file.path(cur.out.folder, paste0("backward_stepwise_selection.txt")))
#model.step <- stepAIC(validation.model, direction = "backward")
#sel.formula <- formula(model.step) # Experimental_Coverage ~ annealing_DeltaG + Position_3terminusLocal + annealing_DeltaG:Position_3terminusLocal
# prepare data: y label
features <- c("Mismatch_pos_1", "Mismatch_pos_2", "Mismatch_pos_3",
              "Mismatch_pos_4", "Mismatch_pos_5", "Mismatch_pos_6",
               "annealing_DeltaG", "Position_3terminusLocal", 
               "Number_of_mismatches_hexamer")
validation.matrix.mod <- validation.matrix[,features]
validation.matrix.mod[, "annealing_DeltaG:Position_3terminusLocal"] <- validation.matrix.mod$annealing_DeltaG * validation.matrix.mod$Position_3terminusLocal
validation.matrix.mod$y <- validation.matrix$Experimental_Coverage
# add crossTerm
model.step <- bestglm(Xy = validation.matrix.mod,
            family = binomial,          
            IC = "AIC",                
            method = "exhaustive")
sink()
model.terms <- gsub("`", "", attr(model.step$BestModel$terms, "term.labels"))
sel.formula <- as.formula(paste0("Experimental_Coverage ~", paste0(model.terms, collapse = "+")))
sink(file.path(cur.out.folder, paste0("feature_significance_TMM.txt")))
print(summary(model.step))
sink(NULL)
p.cut <- 0.05 / length(model.step$coefficients)
p.vals <- summary(model.step)$coefficients[, "Pr(>|z|)"]
# show significant p values
print(paste0("Signifcant features for TMM model: ", paste0(names(p.vals)[which(p.vals < p.cut)], collapse = ",")))
# b) Train model on the train data
train.model <- glm(sel.formula, family = "binomial", data = train.matrix)
# c) Predict and check performance
p <- predict(train.model, newdata = test.matrix, type = "response")
pr <- prediction(p, test.matrix$Experimental_Coverage, label.ordering = c("Unamplified", "Amplified"))
prf <- performance(pr, measure = "tpr", x.measure = "fpr")
png(file.path(cur.out.folder, "TMM_ROC_performance.png"))
plot(prf)
dev.off()
# d) build full model for later interpretation of TMM model
full.model <- glm(sel.formula, family = "binomial", data = feature.matrix)
########
# Evaluate models
##########
# Determine AUC of each model on the test data set
# FE
pr.FE <- prediction(-test.matrix$annealing_DeltaG, test.matrix$Experimental_Coverage, label.ordering = c("Unamplified", "Amplified"))
# DE
pr.DE <- prediction(test.matrix$primer_efficiency, test.matrix$Experimental_Coverage, label.ordering = c("Unamplified", "Amplified"))
# TMM
p <- predict(train.model, newdata = test.matrix, type = "response")
pr.TMM <- prediction(p, test.matrix$Experimental_Coverage, label.ordering = c("Unamplified", "Amplified"))
# Compute AUCs
auc.preds <- list(pr.TMM, pr.DE, pr.FE)
names(auc.preds) <- c("TMM", "DE", "FE")
auc.result <- as.data.frame(get.AUCs(auc.preds))
write.csv(auc.result, file.path(cur.out.folder, "model_AUCs.csv"))
#######################
# Determine significance of ROC curves
#######################
# DeltaG vs TMM
test.result.G.vs.TMM <- wilcox.test(test.matrix$annealing_DeltaG, p, paired = TRUE)
# TMM vs DECIPHER
test.result.TMM.vs.DE <- wilcox.test(test.matrix$primer_efficiency, p, paired = TRUE)
########
library(pROC)
roc.deltaG <- roc(test.matrix$Experimental_Coverage, -test.matrix$annealing_DeltaG, levels = c("Unamplified", "Amplified"))
roc.TMM <- roc(test.matrix$Experimental_Coverage, p, levels = c("Unamplified", "Amplified"))
roc.DE <- roc(test.matrix$Experimental_Coverage, test.matrix$primer_efficiency, levels = c("Unamplified", "Amplified"))
roc.difference <- roc.test(roc.TMM, roc.DE)
######################
# Classifiers Creation via Cutoff Optimization
####################
spec.cutoff <- 0.01 # high-specificity cutoff: FPR < 1%
both.idx <- c(set.indices$train, set.indices$validation)
tv.matrix <- feature.matrix[both.idx,] # train + validation data for DE & FE cutoff optimization
# FREE ENERGY MODEL (FE)
pr <- prediction(-tv.matrix$annealing_DeltaG, tv.matrix$Experimental_Coverage, label.ordering = c("Unamplified", "Amplified"))
cutoffs.FE <- select.optimal.cutoffs(pr, spec.cutoff)
# cutoffs need to be negated as DeltaG is negative
cutoffs.FE$s <- -cutoffs.FE$s 
cutoffs.FE$Y <- -cutoffs.FE$Y 
# DECIPHER MODEL (DE)
pr <- prediction(tv.matrix$primer_efficiency, tv.matrix$Experimental_Coverage, label.ordering = c("Unamplified", "Amplified"))
cutoffs.DE <- select.optimal.cutoffs(pr, spec.cutoff)
# TMM MODEL
# For TMM: perform cross-validation on the validation data set (not allowed to touch training data)
ctrl <- cv_setup()
cv.model <- train_model_new(validation.matrix, "Logistic", NULL, ctrl, sel.formula, type = "CV")
cv.preds <- cv.model$pred$Amplified
pr <- prediction(cv.preds, cv.model$pred$obs, label.ordering = c("Unamplified", "Amplified"))
cutoffs.TMM <- select.optimal.cutoffs(pr, spec.cutoff)
#############
# Evaluate classifiers
##########
# gather all predictions
all.preds <- data.frame("TMM" = predict(train.model, newdata = test.matrix, type = "response"),
                        "DE" = test.matrix$primer_efficiency,
                        "FE" = -test.matrix$annealing_DeltaG)
# create a data frame containing the two cutoffs for each model
cutoffs <- data.frame("TMM" = c(cutoffs.TMM$s, cutoffs.TMM$Y), "DE" = c(cutoffs.DE$s, cutoffs.DE$Y), "FE" = c(-cutoffs.FE$s, -cutoffs.FE$Y)) 
rownames(cutoffs) <- c("s", "Y")
# store cutoff table
write.csv(cutoffs, file.path(cur.out.folder, "classifier_cutoffs.csv"))
# Store model comparison results according to sensitivity and specificity
plot_comparison(test.matrix, all.preds, cutoffs, cur.out.folder)
######
# Visualization of TMM model
#####
plot_3d_model(my_predict, full.model, cur.out.folder)
snapshot3d(file.path(cur.out.folder, "TMM_model_cube.png")) # nb: may need to manually click on the image view to store to disk
if (FALSE) {
    # manually select some nice views and store them
    # Save the list to a text file
    view.folder <- "views"
    nbr.views <- 4
    pp <- par3d(no.readonly=TRUE)
    dput(pp, file = views[1], control = "all")
    pp <- par3d(no.readonly=TRUE)
    dput(pp, file = views[2], control = "all")
    pp <- par3d(no.readonly=TRUE)
    dput(pp, file = views[3], control = "all")
    pp <- par3d(no.readonly=TRUE)
    dput(pp, file = views[4], control = "all")
    views <- sapply(seq_len(nbr.views), function(x) file.path(view.folder, paste0("model_view_", x, ".R")))
    store_views_to_disk(views, my_predict, full.model, cur.out.folder)
}
