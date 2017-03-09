import facebook
import requests
import pandas as pd

j = 50
def get_data(post):
    #print(post['created_time'])
    kplc_post.append(post['message'])
    kplc_id.append(post['id'])
    kplc_time.append(post['created_time'])
def post_to_csv():
    #time_now = datetime.datetime.now()
    data=[]
    for i in range (0,j):
        if kplc_id[i] > since_Id:
            data.append([kplc_post[i], kplc_id[i], kplc_time[i]])
        else:
            break
    dataframe=pd.DataFrame(data,columns=['message', 'id', 'created_time'])
    dataframe.to_csv("%s_posts.csv"%('posts'),index=False)

access_token = '1838162886420379|1a9ae182f2a68c3a4dd20665bd8395a4'
# Look at Kenya Power's profile.
user = 'KenyaPowerLtd'

data=pd.read_csv("%s_posts.csv"%(posts))
since_Id=data['id'].loc[0]

graph = facebook.GraphAPI(access_token)
profile = graph.get_object(user)
posts = graph.get_connections(profile['id'], 'posts')
kplc_post=[]
kplc_id=[]
kplc_time=[]

for i in range (0,j):
    try:
        # Perform some action on each post in the collection we receive from
        # Facebook.
        [get_data(post=post) for post in posts['data']]
        # Attempt to make a request to the next page of data, if it exists.
        posts = requests.get(posts['paging']['next']).json()
    except KeyError:
        # When there are no more pages (['paging']['next']), break from the
        # loop and end the script.
        break
post_to_csv()