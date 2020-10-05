from firebase import firebase

class FirebaseDB():

    def insert():
        firebase = firebase.FirebaseApplication('https://movieapplication-248de.firebaseio.com/', None)

        data = {
            'name':'siema',
            'text':'elo'
        }

        firebase.post('/movieapplication-248de/nowa', data)

    def retreive():
        firebase = firebase.FirebaseApplication('https://movieapplication-248de.firebaseio.com/', None)
        firebase.get('/movieapplication-248de/nowa', '')

    def update():
        firebase = firebase.FirebaseApplication('https://movieapplication-248de.firebaseio.com/', None)
        firebase.put('/movieapplication-248de/nowa/userID', 'login', 'nowylogin')

    def delete():
        firebase = firebase.FirebaseApplication('https://movieapplication-248de.firebaseio.com/', None)
        firebase.delete('/movieapplication-248de/nowa', 'userID')
