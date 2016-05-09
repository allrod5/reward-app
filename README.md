# reward-app

App to process invitations, calculate costumers score and rank them.

## Running the app

### Setup

When you first clone this repository, run:

```sh
lein setup
```

### Environment

To start running the app, run:

```sh
lein run
```

It will create a web server at <http://localhost:3000>.

In there there are two endpoints: *rank* and *invite*.

**rank endpoint**: This endpoint captures a GET request to  <http://localhost:3000/rank> and returns a JSON array of costumers and their scores in decrescent order of their scores. Note that the order of costumers with the same score is arbitrary.

**invite endpoint**: This endpoint captures a POST request to <http://localhost:3000/invite/:inviter/:invited>, where `:inviter` and `invited` are the ids of, respectively, the inviting costumer and the invited one; and returns a message "Invite received!" if the invite was successfully processed.


## How it works

### Idea

The first phase of building the app was to grasp the requested system and to outline how it should work. For that some diagrams were drafted:

[Diagram 1](diagram-1.jpg)
[Diagram 2](diagram-2.jpg)
[Diagram 3](diagram-3.jpg)

### Structure

The core mechanics of the app are in the file `src/reward_app/controller.clj` and the endpoints are in the file `src/reward_app/endpoint/endpoint.clj`.

### Methodology

As it was specified in the diagrams, in order to rank costumers, each invitation needs to be processed, it's processing involves:
 1. Checking if the inviter was already invited by someone (costumer "1" already starts as invited), otherwise the invite is invalid;
 2. Checking if the invited person isn't a costumer already;
 3. Confirming invitations; and
 4. Propagate score rewards over costumers related to each new invitation.

After that ranking costumers is just to sort them by their score.

To carry out steps 1 and 2 it is necessary to keep a list of costumers and for steps 3 and 4 it is necessary to keep a record of invitations made, the score of each costumer and their status as confirmed or not, all of these suggests the use of a tree struture to keep record of all these data and relations. In the app these info are stored in a vector of maps `record` which implies an implicit tree structure as each map contains the keys `:id`, `:score` and `children` and their values are, respectively, a string, a real number and a set of strings in which each string denotes the id of a costumer invited by this costumer.

The app also allows adding new invitations through the *invite* endpoint. It is worth to notice that this implies in rendering some parts of the app non-functional as the new invitation is supposed to be accounted on subsequent ranking requests in the *rank* endpoint. This is because the `record` vector used by the *rank* endpoint is not immutable anymore and there is no way to transform this non-functional scenario in a functional one without great effort.

To get arround the issue with referential transparency yielded by the mutable nature of `record` one can use some of the ready-to-consume solutions provided by Clojure: Refs and Transactions; Agents; or Atoms. For its simplicity and fitness for our needs in this app var holding `record` within an atom is created and it is derrefenced by the *rank* endpoint when needed.

OBS.: In this app `record` information is not persisted to a database, but it was the case, it would be possible to avoid having `record` as a variable as it would be possible to always construct the `record` vector from the database and it would be always updated (notice that this doesn't solve the referential transparency issue as now we are persisting to a database which is a side-effect and renders the portion of the code that deals with the database non-functional too), but it wouldn't be as efficient as keeping `record` on primary memory and not to need to access secondary memory at each HTTP request, so even if a database to persist `record` data were to be introduced in this app, the *modus operandis* of the app should remain identical.

PS.: Specifics of the code are explained by the comments in the code itself.

## Notes

It is curious that by the problem definition nothing impedes a costumer of inviting himself or his parent to render himself as a confirmed costumer. 
