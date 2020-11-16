library(ggplot2)

data <- read.csv("distributionTags.csv")

data$label <- factor(data$label, levels = data$label[order(data$order)])

ggplot(data=data, aes(x=label, y=number, fill=class)) +
  geom_bar(stat="identity") +
  geom_text(aes(label=number), vjust=-0.3, size=6.5) +
  xlab("Number of Tags") + ylab("Number of libraries") +
  theme_minimal() +
  theme(text=element_text(size=21), legend.position="None")
