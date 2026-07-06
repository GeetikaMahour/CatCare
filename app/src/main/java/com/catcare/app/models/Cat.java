package com.catcare.app.models;

public class Cat {
    private String id;
    private String name;
    private String breed;
    private float age;
    private float weight;
    private String gender;
    private String photoUri;
    private long createdAt;

    public Cat() {}

    public Cat(String id, String name, String breed, float age,
               float weight, String gender, String photoUri) {
        this.id = id;
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.weight = weight;
        this.gender = gender;
        this.photoUri = photoUri;
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getBreed() { return breed; }
    public float getAge() { return age; }
    public float getWeight() { return weight; }
    public String getGender() { return gender; }
    public String getPhotoUri() { return photoUri; }
    public long getCreatedAt() { return createdAt; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setBreed(String breed) { this.breed = breed; }
    public void setAge(float age) { this.age = age; }
    public void setWeight(float weight) { this.weight = weight; }
    public void setGender(String gender) { this.gender = gender; }
    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public String getGenderEmoji() {
        return "male".equals(gender) ? "🐱" : "🐈";
    }
}