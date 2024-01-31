public enum CopyStatus {
    SUCCESS("Успешно скопирован."),
    PASSED("Уже имеется в месте назначения."),
    SKIPPED("Не является регулярным файлом."),
    ERROR("Ошибка.");
    private final String name;
    public String getName(){
        return name;
    }
    private CopyStatus(String name){
        this.name = name;
    }
}
