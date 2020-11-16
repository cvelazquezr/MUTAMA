import pandas as py
import numpy as np
from gensim.models import Word2Vec
import pickle

from sklearn.svm import SVC


def text2vec(textual_data: list, model: Word2Vec):
    vectors = list()

    for line in textual_data:
        vector_line = np.array([model.wv[token] for token in line])
        
        vector_avg = sum(vector_line) / len(line)
        vectors.append(np.array(vector_avg))
    
    return np.array(vectors)


def tags2classes(remaining_tags: list, tags_info: list):
    tags_categorized = list()

    for tag_line in tags_info:
        tagged_line = [0] * len(remaining_tags)

        for tag in tag_line:
            if tag in remaining_tags:
                tagged_line[remaining_tags.index(tag)] = 1
        tags_categorized.append(np.array(tagged_line))
    
    return np.array(tags_categorized)


def tags_stats(tags_info: list, threshold: int):
    tags_counter = dict()

    for tag_line in tags_info:
        for tag in tag_line:
            if tag in tags_counter:
                tags_counter[tag] += 1
            else:
                tags_counter[tag] = 1
    
    tags_counter = {k: v for k, v in sorted(tags_counter.items(), key=lambda item: item[1], reverse=True)}

    remaining_tags = list()

    counter = 0
    for k, v in tags_counter.items():
        if v > threshold:
            remaining_tags.append(k)
            counter += 1

    print(f"Only {len(remaining_tags)} tags remain out of {len(tags_counter)}")
    return remaining_tags
    

def filter_text(text_information: list, transformed_tags: np.array):
    filtered_transformations = list()
    filtered_text = list()

    for i, transformation in enumerate(transformed_tags):
        if transformation.any() and len(text_information[i]):
            filtered_transformations.append(transformation)
            filtered_text.append(text_information[i])
    
    filtered_transformations = np.array(filtered_transformations)

    return filtered_text, filtered_transformations


def to_arff(data: np.array, classes: np.array, path: str):
    f = open(path, "w")
    header_classes = classes.shape[1]
    header_data = data.shape[1]
    f.write(f"@relation \'Maven: -C {header_classes}\'\n\n")

    # Write headers
    for i in range(header_classes):
        f.write(f"@attribute X{i} {{0,1}}\n")
    
    for i in range(header_data):
        f.write(f"@attribute X{i + header_classes} numeric\n")
    
    # Write data
    f.write("\n@data\n")

    for i in range(classes.shape[0]):
        classes_current = ",".join([str(number) for number in classes[i]])
        data_current = ",".join([str(number) for number in data[i]])

        f.write(classes_current + "," + data_current + "\n")
    f.close()


if __name__ == "__main__":
    textual_information = list()
    tags_information = list()

    print("Reading file ...")
    with open("tags_libraries_extracted/gt_three_tags.txt") as f:
        while True:
            line = f.readline()
            if not line:
                break
            else:
                line = line.strip()
                line = line.split(",")
                textual_information.append(line[0].split())
                tags_information.append(line[1].split())
    print("Done !")

    print("Making analysis of the tags ...")
    remaining_tags = tags_stats(tags_information, 10)

    pickle.dump(remaining_tags, open("remaining_tags.pickle", "wb"))

    print("Transforming the tags into labels to predict ...")
    transformed_tags = tags2classes(remaining_tags, tags_information)

    print("Filtering out the text where the values on the tags are zeros ...")
    filtered_textual, filtered_tags = filter_text(textual_information, transformed_tags)

    # Saving WordVec model
    model = Word2Vec(filtered_textual, min_count=1)
    # model.save("trained_model.w2v")

    # Loading trained Word2Vec model
    print("Loading model and transforming the data ...")
    # model = Word2Vec.load("trained_model.w2v")
    vectors = text2vec(filtered_textual, model)
    print("Done!")

    # Training
    X = vectors
    y = filtered_tags

    print(X.shape, y.shape)

    print("Saving information to an ARFF file ...")
    to_arff(X, y, "arffs/gt_three_tags.arff")
