import pandas as pd
import numpy as np
from matplotlib import pyplot as plt
import seaborn as sns
from .models import Profile
from sklearn.model_selection import train_test_split
from sklearn.neighbors import KNeighborsClassifier
from sklearn.cluster import KMeans
from sklearn.metrics import confusion_matrix
from joblib import dump, load
from .my_database import DatabaseHelper

class Knn():
    def preprocessData(df, list, col):
        df[col] = df[col].apply(lambda x: list.index(df[col][df[col].tolist().index(x)]))

    def cos():
        cols = [field.name for field in Profile._meta.get_fields()]
        cols = cols[2:]
        cols.sort()
        data = DatabaseHelper.getProfiles()
        df = pd.DataFrame(data, columns=cols)
        pd.set_option('display.max_rows', None, 'display.max_columns', None)
        #print(df.describe())
        print(df)
        columns = len(cols)
        sizeDf = df.size

        base = DatabaseHelper.getBase()
        headers = cols
        headers.remove("age")
        headers.remove("oscar")
        signleHeaders = ["group", "sex"]
        
        y = 0
        x = 0
        while x < len(headers):
            if (headers[x] == "sex" or headers[x] == "group" or headers[x] == "place"):
                Knn.preprocessData(df, base[y], headers[x])
                y += 1
                x += 1
            else:
                for z in range(3):
                    Knn.preprocessData(df, base[y], headers[x])
                    x += 1
                y += 1

        #print(df.describe())
        print(df)
        
        rows = sizeDf/columns
        rowsLim = int(rows/2)
        
        X_train = df.iloc[:rowsLim, :]
        X_test = df.iloc[rowsLim:, :]
        
        kmeans = KMeans(n_clusters=4)
        kmeans.fit(X_train)
        y_kmeans = kmeans.predict(X_train)
        print(y_kmeans)
    
        m = KNeighborsClassifier()
        m.fit(X_train, y_kmeans)

        y_predicted = m.predict(X_test)
        print(y_predicted)
        print(m.score(X_test, y_predicted)) 

        #y = df[['class_encod']] # target attributes 
        #X = df.iloc[:, 0:4] # input attributes
        #print(X.head())

        #X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=0, stratify=y)
        #np.shape(y_train)

        #m = KNeighborsClassifier()
        #m.fit(X_train, np.ravel(y_train))

        #y_predicted = m.predict(X_test.iloc[0:10]) 

        #m.score(X_test, y_test)

        #dump(m, 'iris-classifier.dmp')
        #ic = load('iris-classifier.dmp')
        #confusion_matrix(y_test, ic.predict(X_test))
            