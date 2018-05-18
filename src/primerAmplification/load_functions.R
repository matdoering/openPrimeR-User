get_train_indices_new <- function(feature.matrix, seed = 12345) {
    set.seed(seed = seed)
    # new function: don't sample (we have cross-term in the model to correct for bias); also: use validation set
    ########
    # bias: more observations from openPrimeR set -> full model will be 'overtrained' for openPrimeR distribution!??!?!
    # controlling for equal distribution of "Run": from 300 (equal distribution of DeltaG) to 168 samples??
    #######
    set.percentages <- c("validation" = 0.25, "training" = 0.5, "test" = 0.25)
    set.sizes <- set.percentages * nrow(feature.matrix)
    names(set.sizes) <- names(set.percentages)
    available.idx <- seq_len(nrow(feature.matrix))
    set.indices <- list()
    for (i in seq_along(set.sizes)) {
        set.name <- names(set.sizes)[i]
        sel.idx <- sample(available.idx, set.sizes[i])
        set.indices[[set.name]] <- sel.idx
    }
    return(set.indices)
}
cv_setup <- function() {
    n.folds <- 5 # number of cv folds
    n.times <- 10 # number of cv repetitions
    # assign cv folds always in the same way (non-random)
    set.seed(1) 
    seeds <- vector(mode = "list", length = n.folds * n.times + 1)
    for(i in seq_len(n.folds *n.times)) {
        seeds[[i]] <- sample.int(1000, 1) # set a seed for each model in each folding
    }
    ## For the last model:
    seeds[[(n.folds * n.times + 1)]] <- sample.int(1000, 1) # only one model as the last model
    ctrl <- trainControl(method = "repeatedcv", number = n.folds, repeats = n.times, savePredictions = TRUE, classProbs = TRUE, seeds = seeds) # use only training data for training ;-)
    return(ctrl)
}
train_model_new <- function(data, data.ident, train.idx, ctrl, formula, type = "CV") {
    model <- NULL
    if (type == "CV") {
        print(data.ident)
        model <- train(Experimental_Coverage ~ annealing_DeltaG + log(Position_3terminus, 2), data = data, method = "glm", family="binomial", trControl = ctrl, subset = train.idx)
    }
    return(model)
}
########
plot.heatmap.pred <- function(lex.df, primer.df, sel,cur.out.folder) {
    be.sparse <- FALSE
    if (length(primer.df) == 0 || nrow(primer.df) == 0 || length(lex.df) == 0) {
        return(NULL)
    }
    if (length(sel) == 0) {
        sel <- 1:nrow(lex.df)
    }
    # create a layout for plotting
    new.primer.order <- NULL 
    colors <- brewer.pal(8, "Accent")[c(8,5)]
    g.df <- lex.df[sel, ]
    if (nrow(g.df) == 0) {
        return(NULL)
    }
    m <- matrix(rep(0, nrow(g.df) * nrow(primer.df)), nrow = nrow(g.df), ncol = nrow(primer.df))
    rownames(m) <- g.df$ID
    colnames(m) <- primer.df$ID
    for (i in 1:nrow(primer.df)) {
        entry <- primer.df[i,]
        seq.identifiers <- as.numeric(unlist(strsplit(entry$Covered_Seqs, split = ",")))
        mp <- match(seq.identifiers, g.df$Identifier)
        m[mp,i] <- 1 # covered
    } 
    # deal with the sparse matrix -> remove column/rows all zero
    if (be.sparse) {
        sel.row <- which(apply(m, 1, function(x) any(x != 0)))
        sel.col <- which(apply(m, 2, function(x) any(x != 0)))
    } else {
        sel.row <- 1:nrow(m)
        sel.col <- 1:ncol(m)
    }
    m <- m[sel.row, sel.col, drop=FALSE]
    if (all(m == 0)) { # can't be plotted
        return(NULL)
    }
    lmat <- rbind(3,1,4,2) # structure of heatmap elements 1,4,2,3
    lhei <- c(0.6,4,1,0.6) # size of these elements: plot, legend, dendrogram1, dendrogram2 4,1,0.6,0.6
    tick.fun <- NULL
    if (all(m == 0)) {
        tick.fun <- function(){ 
        key.breaks <- parent.frame()$breaks
        return(list(
          at=c(0,0),
          labels=c(ifelse(key.breaks[1] == 0, "Not covered", "Covered") ,ifelse(key.breaks[length(key.breaks)] == 0, "Not covered", "Covered"))
        ))}
    } else {
        tick.fun <- function(){
        key.breaks <- parent.frame()$breaks
        return(list(
        at=parent.frame()$scale01(c(key.breaks[1],key.breaks[length(key.breaks)])),
        labels=c(ifelse(key.breaks[1] == 0, "Not covered", "Covered") ,ifelse(key.breaks[length(key.breaks)] == 0, "Not covered", "Covered"))
        ))}
    }
    if (nrow(m) < 2 || ncol(m) < 2) {
        print("WARNING: can't plot heatmap for matrices smaller than 2x2")
        return(NULL)
    }
    write.csv(m, file.path(cur.out.folder, "heatmap_pred_data.csv"))
    png(file.path(cur.out.folder, "heatmap_pred.png"), width = 800, height = 800)
    heatmap.2(m, cexRow = 1.5, cexCol = 1.5, col = colors, # messing with margins -> figure margins too large hmm # margins: 5,10
            margins = c(5,5), xlab = "", trace = "none", dendrogram = "none", key.xlab = "Coverage Status", 
            key.title = "Color Key", density.info = "none", key = FALSE, lmat = lmat, lwid = c(1), lhei = lhei, 
            key.par=list(mar=c(1,2.5,5,2)), srtCol = 90, #4,3,2,2
            colsep=1:ncol(m), # separate cells
            rowsep=1:nrow(m), main = "Predicted Primer Coverage",
            key.xtickfun=tick.fun, Rowv=FALSE, Colv=FALSE

    )
    dev.off()
    return(NULL)
}
plot.heatmap.exp <- function(feature.matrix, cur.out.folder) {
    library(gtools) # mixedorder function
    be.sparse <- FALSE
    if (length(feature.matrix) == 0 || nrow(feature.matrix) == 0) {
        return(NULL)
    }
    # create a layout for plotting
    library(RColorBrewer)
    # define colors for coverage
    colors <- brewer.pal(8, "Accent")[c(8,5)]
    # create binary matrix of coverage events
    templates.unique <- unique(feature.matrix$Template)
    primers.unique <- unique(feature.matrix$Primer)
    # order primers numerically
    m <- mixedorder(substring(primers.unique, 5, nchar(as.character(primers.unique))))
    primers.unique <- primers.unique[m]
    m <- matrix(rep(0, length(templates.unique) * length(primers.unique)), nrow = length(templates.unique), ncol = length(primers.unique))
    rownames(m) <- templates.unique
    colnames(m) <- primers.unique
    for (i in seq_along(primers.unique)) {
        primer.id <- primers.unique[i]
        entries <- which(feature.matrix$Primer == primer.id)
        template.ids <- feature.matrix$Template[entries]
        covered.idx <- which(feature.matrix$Experimental_Coverage[entries] == "Amplified")
        covered.templates <- template.ids[covered.idx]
        mp <- match(covered.templates, rownames(m))
        m[mp,i] <- 1 # covered
    } 


    # deal with the sparse matrix -> remove column/rows all zero
    if (be.sparse) {
        sel.row <- which(apply(m, 1, function(x) any(x != 0)))
        sel.col <- which(apply(m, 2, function(x) any(x != 0)))
    } else {
        sel.row <- 1:nrow(m)
        sel.col <- 1:ncol(m)
    }
    m <- m[sel.row, sel.col, drop=FALSE]
    if (all(m == 0)) { # can't be plotted
        return(NULL)
    }
    lmat <- rbind(3,1,4,2) # structure of heatmap elements 1,4,2,3
    lhei <- c(0.6,4,1,0.6) # size of these elements: plot, legend, dendrogram1, dendrogram2 4,1,0.6,0.6
    tick.fun <- NULL
    if (all(m == 0)) {
        tick.fun <- function(){ 
        key.breaks <- parent.frame()$breaks
        return(list(
          at=c(0,0),
          labels=c(ifelse(key.breaks[1] == 0, "Not covered", "Covered") ,ifelse(key.breaks[length(key.breaks)] == 0, "Not covered", "Covered"))
        ))}
    } else {
        tick.fun <- function(){
        key.breaks <- parent.frame()$breaks
        return(list(
        at=parent.frame()$scale01(c(key.breaks[1],key.breaks[length(key.breaks)])),
        labels=c(ifelse(key.breaks[1] == 0, "Not covered", "Covered") ,ifelse(key.breaks[length(key.breaks)] == 0, "Not covered", "Covered"))
        ))}
    }
    if (nrow(m) < 2 || ncol(m) < 2) {
        print("WARNING: can't plot heatmap for matrices smaller than 2x2")
        return(NULL)
    }
    #par(mar=c(7,4,4,2)+0.1) 
    #par(mar=c(30,4,4,4)+0.1)
    #par(oma=c(4,2,2,4))
    write.csv(m, file.path(cur.out.folder, "heatmap_exp_data.csv"))
    png(file.path(cur.out.folder, "heatmap_exp.png"), width = 800, height = 800)
    heatmap.data <- heatmap.2(m, cexRow = 1.5, cexCol = 1.5, col = colors, # messing with margins -> figure margins too large hmm # margins: 5,10
            margins = c(5,5), xlab = "", trace = "none", dendrogram = "none", key.xlab = "Coverage Status", 
            key.title = "Color Key", density.info = "none", key = FALSE, lmat = lmat, lwid = c(1), lhei = lhei, 
            key.par=list(mar=c(1,2.5,5,2)), srtCol = 90, #4,3,2,2
            colsep=1:ncol(m), # separate cells
            rowsep=1:nrow(m), main = "Primer coverage overview",
            key.xtickfun=tick.fun, Rowv=FALSE, Colv=FALSE

    )
    dev.off()
}
my_predict <- function(pos, deltaG) {
    # pos: 1,2,3,4,5,6 from 1st to 6th position in 3' hexamer
    # pos: 0 no 3' hexamer mismatch
    my.feat.matrix <- data.frame(#"(Intercept)" = 1,
                              "Position_3terminusLocal" = pos,
                              "annealing_DeltaG" = deltaG)
    p <- predict(full.model, newdata = my.feat.matrix, type = "response")
    return(p)
}
plot_3d_model <- function(f, cvg.model, out.folder, plot.matrix = NULL, 
                          show.points = TRUE, fill.type = "points") {
    # model function (my_predict), CVG_MODEL: model data (training)
    library(rgl)
    ident <- ""
    if (length(plot.matrix) == 0) { 
        # no specific plotting data (features) given
        # -> plot the training data!
        scatter.data <- cvg.model$data
        #posis <- cvg.model$data$Position_3terminus
        posis <- cvg.model$data$Position_3terminusLocal
        deltaG <- cvg.model$data$annealing_DeltaG
    } else {
        # use the data provided in feature matrix for the plot (e.g. the test data)
        scatter.data <- plot.matrix
        #posis <- scatter.data$Position_3terminus
        posis <- scatter.data$Position_3terminusLocal
        deltaG <- scatter.data$annealing_DeltaG
    }
    my_surface <- function(f, step = 0.1, show.points = TRUE){
        x <- seq(min(posis), max(posis), step) # pos
        y <- seq(min(deltaG), max(deltaG), step) # DeltaG 
        z <- outer(x,y,f)
        # add decision boundary at 50% probability
        decision.boundary <- z
        decision.boundary[TRUE,] <- 0.5
        # add rainbow colors:
        nbcol = 100
        library(RColorBrewer)
        color = colorRampPalette(brewer.pal(11,"RdBu"))(nbcol)
        zcol  = cut(z, nbcol)
        # ensure that window is at a larger size for adding the legend (at best, fullscreen)
        windowRect <- c(1920,20, 3840, 1165) # can't be larger than screen :-(
        ######
        open3d(windowRect=windowRect, cex = 2) # set window size and text size
        ####
        # show a ribbon of all possible function values
        # for drawing options: ?rgl.material

        persp3d(x, y, z, xlab = "", zlab = "",
              ylab = "", axes = TRUE, ticktype = "detailed",
              alpha = 0.6,
              col = color[zcol], lit = FALSE, front = fill.type, back = fill.type) # front filled
        # add decision boundary at 50%
        persp3d(x, y, decision.boundary, add = TRUE, alpha = 0.2)
    }
    library(RColorBrewer)
    cols <- brewer.pal(8, "Set1")[1:2]
    col <- rep(cols[1], nrow(scatter.data))
    col[scatter.data$Experimental_Coverage == "Amplified"] <- cols[2]
    my_surface(f, show.points = show.points)
    if (show.points) {
        # show predictions from training the model on the prediction surface
        points3d(z = f(posis, deltaG),
                  x = posis, y = deltaG,
                  col = col, alpha = 0.7, size = 10, point_antialias = TRUE)
    }

    # add legend: IMPORTANT: resize window first (full screen), then plot legend3d!
    legend3d("topright", legend = c("Unamplified", "Amplified"), col =  cols, cex = 3, pch = 15)

} 
store_views_to_disk <- function(views, f, cvg.model, out.folder, plot.matrix = NULL) {
    # load views from disk
    ident <- ""
    if (length(plot.matrix) != 0) {
        ident <- "_non_training_points"
    }
    for (i in seq_along(views)) {
        view <- views[i]
        pp <- dget(view)
        # set some general viewing options
        pp$cex <- 5 # increase text size
        #pp$cex.lab <- 2 # not possible ...
        #pp$windowRect <- pp$windowRect * 2
        plot_3d_model(f, cvg.model, plot.matrix = feature.matrix) 
        # change the view to the selected view
        par3d(pp)
        # increase the size further ...
        out.file <- file.path(out.folder, paste0("decision_surface_", ident, "_", i))
        snapshot3d(filename = paste0(out.file, ".png"))
        # for high resolution: postscript; don't use points -> takes too long -> still, ignores transparency, doesn't look too great.
        plot_3d_model(f, cvg.model, plot.matrix = feature.matrix, fill.type = "filled")
        snapshot3d(filename = paste0(out.file, ".png"))

        #plot_3d_model(f, cvg.model, plot.matrix = feature.matrix, show.points = FALSE)
        #rgl.postscript(paste0(out.file, ".pdf"), fmt = "pdf", drawText = TRUE)
    }
}
plot_scatter_deltaG_pos <- function(feature.matrix, cur.out.folder) {
    library(ggplot2)
    plot.matrix <- feature.matrix
    plot.matrix$Run[plot.matrix$Run == "openPrimeR2017"] <- "Set1"
    plot.matrix$Run[plot.matrix$Run == "Tiller2008_1st"] <- "Set2"
    p2 <- ggplot(plot.matrix, aes(x = Position_3terminus, y = annealing_DeltaG,
                                colour = Experimental_Coverage)) +
        geom_point(aes(shape=Run), alpha = 0.65) + # + facet_grid(. ~ Run) + 
        geom_vline(xintercept = 6, color = "black", linetype = "dotted", alpha = 0.5) + 
        geom_hline(yintercept = -5, color = "black", linetype = "dotted", alpha = 0.75) +
        xlab("Mismatch position closest to the primer 3' terminus") + ylab(expression(paste(Delta, "G"))) + 
        scale_colour_discrete(name = "Amplification Status") + 
        scale_shape_discrete(name = "Primer Set")
    ggsave(file.path(cur.out.folder, "feature_scatter.png"))
    ggsave(file.path(cur.out.folder, "feature_scatter.pdf"))

}

plot_comparison <- function(test.matrix, all.preds, cutoffs, TMM.model) {
    # Determine coverage for every number of maximal mismatches allowed
    library(caret)
    library(RColorBrewer)
    labels <- test.matrix$Experimental_Coverage
    plot.df <- NULL
    for (i in seq_len(ncol(cutoffs))) {
        model.name <- colnames(cutoffs)[i]
        cur.preds <- all.preds[, model.name]
        for (j in seq_len(nrow(cutoffs))) {
            cur.cut <- cutoffs[j,i]
            cut.type <- rownames(cutoffs)[j]
            class.pred <- factor(ifelse(cur.preds >= cur.cut, "Amplified", "Unamplified"), levels = c("Unamplified", "Amplified"))
            conf <- confusionMatrix(class.pred, labels, positive = "Amplified")
            measures <- c("Sensitivity", "Specificity") #, "Accuracy")
            values <- c(conf$byClass["Sensitivity"],  conf$byClass["Specificity"])#,  conf$overall["Accuracy"])
            res.df <- data.frame("Model" = paste0(model.name, "_", cut.type), "Measure" = measures, "Value" = values)
            plot.df <- rbind(plot.df, res.df)
        }
    }
    opti.type <- rep(NA, nrow(plot.df))
    s.idx <- grep("_s", plot.df$Model)
    y.idx <- grep("_Y", plot.df$Model)
    opti.type[s.idx] <- "High-specificity cutoffs"
    opti.type[y.idx] <- "High-performance cutoffs"
    plot.df$OptiType <- opti.type
    # change model names for subscripts 
    plot.df$Model <- factor(paste0(gsub("_", "[", plot.df$Model), "]"), levels = c("TMM[s]", "TMM[Y]", "DE[s]","DE[Y]","FE[s]","FE[Y]"))
    #title <- "Classifier performance"
    p.overall  <- ggplot(plot.df) + 
        geom_bar(position = "dodge", stat = "identity", aes(x = Model, y = Value, fill = Measure, group = Measure)) +
        #ggtitle(title) +
        scale_y_continuous(limits = c(0, 1),
                           labels = scales::percent) + 
        #theme(axis.text.x = element_text(
            #angle = 90, 
            #hjust = 1, vjust = 0.5)) +
        ylab("Agreement of predicted and experimental amplification status") +
        # add numbers above bars
        geom_text(aes(label = paste0(round(Value*100,0),"%"), x = Model, y = Value, group = Measure),
                    position=position_dodge(width=0.9), vjust=-0.25) +
        facet_grid(. ~ OptiType, scales = "free_x") +
        scale_fill_brewer(palette="Paired") +
        scale_x_discrete(labels = c('TMM[s]' = parse(text = expression("TMM[s]")), 
                                    "TMM[Y]" = parse(text = expression("TMM[Y]")),
                                    "DE[s]" = parse(text = expression("DE[s]")),
                                    "DE[Y]" = parse(text = expression("DE[Y]")),
                                    "FE[s]" = parse(text = expression("FE[s]")),
                                    "FE[Y]" = parse(text = expression("FE[Y]"))
                                    )) + 
      theme(text = element_text(size=15))

    ggsave(file.path(cur.out.folder, paste0("Accuracy_overall.png")))
    ggsave(file.path(cur.out.folder, paste0("Accuracy_overall.pdf")))
    return(p.overall)
}

select.optimal.cutoffs <- function(pr, spec.cutoff) {
    prf <- performance(pr, measure = "tpr", x.measure = "fpr")
    plot(prf)
    auc <- performance(pr, measure = "auc")
    auc <- auc@y.values[[1]] # 0.927
    # select two params, one maximizing specificity, the other maximizing both
    G.cutoff.spec <- prf@alpha.values[[1]][tail(which(prf@x.values[[1]] <= spec.cutoff), n = 1)] # -10.168
    # use Youden's index: sensitivity + specificity - 1
    youden.idx <- (1 - prf@x.values[[1]]) + prf@y.values[[1]] - 1
    G.cutoff.y <- prf@alpha.values[[1]][which.max(youden.idx)]
    result <- list("Y" = G.cutoff.y, "s" = G.cutoff.spec, "AUC" = auc)
    return(result)
}

iqr <- function(x) {
    paste0("[", quantile(x, 0.25), ", ", quantile(x, 0.75), "]")
}

get.AUCs <- function(auc.preds) {
    out <- vector("list", length(auc.preds))
    for (i in seq_along(auc.preds)) {
       auc <- performance(auc.preds[[i]], measure = "auc")
       auc <- auc@y.values[[1]] # 0.946 
       out[[i]] <- auc
    }
    names(out) <- names(auc.preds)
    return(out)
}
