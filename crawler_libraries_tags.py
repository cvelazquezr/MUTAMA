import time
import requests
import pandas as pd
from bs4 import BeautifulSoup
from random import randint


if __name__ == "__main__":

    data = pd.read_csv("librariesTags.csv")
    groups = data["groupID"].tolist()
    artifacts = data["artifactID"].tolist()

    base_url = "https://mvnrepository.com/artifact/"
    
    groupIDs = list()
    artifactIDs = list()
    tags = list()


    for number, (group, artifact) in enumerate(zip(groups, artifacts)):
        url_to_query = base_url + group + "/" + artifact
        print(f"{number + 1}/{len(groups)}", "Processing: ", url_to_query)
        delay = randint(3, 10)

        time.sleep(delay)
        response = requests.get(url_to_query)

        if response.status_code == 200:
            html = response.text
            soup = BeautifulSoup(html, 'html.parser')

            # Tags
            tags_html = soup.find_all('a', {'class': 'tag'})
            tags_texts = [tag.text for tag in tags_html]
            tags.append(" ".join(tags_texts))
            
            # Adding basic info
            groupIDs.append(group)
            artifactIDs.append(artifact)

            data_to_write = {"groupID": groupIDs,
                            "artifactID": artifactIDs,
                            "tags": tags}
    
            df = pd.DataFrame(data=data_to_write)
            df.to_csv("tagsLibraries.csv", index=False)
        else:
            print("Not successful :(")
            continue
    