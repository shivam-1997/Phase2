import random
import numpy as np

n_students = 1000
n_profs = 50
n_projects = 1000
maxProjUnderProf = 5

def generateStudents(n):
    count = 1
    students = list()
    
    with open("students.txt", "w") as f:
        genderList = ['m', 'f']
        for i in range(n):
            roll = "st_" + str(i)
            gender = random.choice(genderList)
            cpi = round(random.uniform(5.0,10.0), 2)
            print(roll, gender, cpi, sep='\t', file=f)
            students.append(roll)
    
    return students

def generateProfs(n):
    profs = list()
    with open("profs.txt", "w") as f:
        genderList = ['m', 'f']
        for i in range(n):
            roll = "prof_" + str(i)
            gender = random.choice(genderList)
            print(roll, gender, sep='\t', file=f)
            profs.append(roll)

    return profs

def generateProjects(n):
    count = 1
    projects = list()
    with open("projects.txt", "w") as f:
        for i in range(n):
            roll = "proj_" + str(i)
            print(roll, file=f)
            projects.append(roll)

        
    return projects

def generateLinks(students, profs, projects):
    # for time being simple link
    # from student to student
    # generating 1lac links
    with open("relations.txt", "w") as f:
        for i in range(100000):
            a = random.randrange(0,n_students, 3)
            b = random.randrange(0,n_students, 3)
            print(a,b, sep='\t', file=f)

def generateProfProjLinks(profs, projects):
    # a prof can have [0, maxProjUnderProf] projects
    with open("prof_proj_relations.txt", "w") as f:
        for i in range(n_profs):
            prof = "prof_" + str(i)
            n = random.randrange(0, maxProjUnderProf+1)
            for j in range(n):
                print(prof, random.choice(projects), sep='\t', file=f)

def generateStudProjLinks(students, projects):
    # for now assume each student has to have a project assigned
    with open("student_proj_relations.txt", "w") as f:
        for i in range(n_students):
            st = "st_"+ str(i)
            print(st, random.choice(projects), sep='\t', file=f)

if __name__ == "__main__":
    students = generateStudents(n_students)
    profs = generateProfs(n_profs)
    projects = generateProjects(n_projects)
    # generateLinks(students, profs, projects)
    # prof can have 0 or more projects, say at max 8
    generateProfProjLinks(profs, projects)
    generateStudProjLinks(students, projects)
