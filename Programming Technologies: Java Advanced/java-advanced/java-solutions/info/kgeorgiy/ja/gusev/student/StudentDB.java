package info.kgeorgiy.ja.gusev.student;

import java.util.*;
import java.util.stream.Collectors;
import java.util.function.BinaryOperator;

import info.kgeorgiy.java.advanced.student.Group;
import info.kgeorgiy.java.advanced.student.GroupName;
import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentQuery;
import info.kgeorgiy.java.advanced.student.GroupQuery;

public class StudentDB implements StudentQuery, GroupQuery {

    
    
    @Override
    public List<String> getFirstNames(List<Student> students) {
        
        return students.stream().map(Student::getFirstName).collect(Collectors.toList());
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        
        return students.stream().map(Student::getLastName).collect(Collectors.toList());
    }

    @Override
    public List<GroupName> getGroups(List<Student> students) {
        
        return students.stream().map(Student::getGroup).collect(Collectors.toList());
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        
        return students.stream().map(s -> s.getFirstName() + " " + s.getLastName()).collect(Collectors.toList());
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        
        return students.stream().map(Student::getFirstName).collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String getMaxStudentFirstName(List<Student> students) {
        
        return students.stream().max(Comparator.comparingInt(Student::getId)).map(Student::getFirstName).orElse("");
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        
        return students.stream().sorted(Comparator.comparingInt(Student::getId)).collect(Collectors.toList());
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        
        return students.stream()
                .sorted(Comparator.comparing(Student::getLastName)
                .thenComparing(Student::getFirstName)
                .thenComparing(Student::getId, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        
        return students.stream()
                .filter(s -> s.getFirstName().equals(name))
                .sorted(Comparator.comparing(Student::getLastName)
                .thenComparing(Student::getFirstName)
                .thenComparing(Student::getId, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        
        return students.stream()
                .filter(s -> s.getLastName().equals(name))
                .sorted(Comparator.comparing(Student::getLastName)
                .thenComparing(Student::getFirstName)
                .thenComparing(Student::getId, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, GroupName group) {
        
        return students.stream()
                .filter(s -> s.getGroup().equals(group))
                .sorted(Comparator.comparing(Student::getLastName)
                .thenComparing(Student::getFirstName)
                .thenComparing(Student::getId, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, GroupName group) {
        
        return findStudentsByGroup(students, group).stream()
                .collect(Collectors.toMap(Student::getLastName, Student::getFirstName, BinaryOperator.minBy(String::compareTo)));
    }

    

    @Override
    public List<Group> getGroupsByName(Collection<Student> students) {
        
        return students.stream()
                .collect(Collectors.groupingBy(Student::getGroup)) 
                .entrySet().stream() 
                .map(entry -> new Group(entry.getKey(), sortStudentsByName(entry.getValue()))) 
                .sorted(Comparator.comparing(Group::getName)) 
                .collect(Collectors.toList());
    }

    @Override
    public List<Group> getGroupsById(Collection<Student> students) {
        
        return students.stream()
                .collect(Collectors.groupingBy(Student::getGroup)) 
                .entrySet().stream() 
                .map(entry -> new Group(entry.getKey(), sortStudentsById(entry.getValue()))) 
                .sorted(Comparator.comparing(Group::getName)) 
                .collect(Collectors.toList());
    }

    @Override
    public GroupName getLargestGroup(Collection<Student> students) {
        
        return students.stream()
                .collect(Collectors.groupingBy(Student::getGroup, Collectors.counting())) 
                .entrySet().stream() 
                .max(Comparator.comparingLong(Map.Entry<GroupName, Long>::getValue) 
                .thenComparing(Map.Entry::getKey)) 
                .map(Map.Entry::getKey)
                .orElse(null); 
    }


    @Override
    public GroupName getLargestGroupFirstName(Collection<Student> students) {
        
        return students.stream()
                .collect(Collectors.groupingBy(Student::getGroup,
                        Collectors.collectingAndThen(Collectors.mapping(Student::getFirstName, Collectors.toSet()), Set::size))) 
                .entrySet().stream() 
                .max(Comparator.comparingInt(Map.Entry<GroupName, Integer>::getValue) 
                .thenComparing(Map.Entry::getKey, Comparator.reverseOrder())) 
                .map(Map.Entry::getKey)
                .orElse(null); 
    }
}

