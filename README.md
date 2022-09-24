# jenkinsfile git push wont work

Nothing to commit, working tree clean #23135

Make new repo and push the existing code again to the new repo

git init doesn’t initialize if you already have a .git/ folder in your repository. So, for your case, do -

(1) rm -rf .git/

(2) git init

(3) git remote add origin https://repository.remote.url

(4) git commit -m “Commit message”

(5) git push -f origin master

Note that all git configs like remote repositories for this repository are cleared in step 1. So, you have to setup all remote repository URLs again.

Also, take care of the -f in step 5 : The remote already has some code base with n commits, and you’re trying to make all those changes into a single commit. So, force-pushing the changes to remote is necessary.
