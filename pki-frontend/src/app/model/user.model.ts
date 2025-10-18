export interface UserDTO{
    id: number;
    email: string;
    firstName: string;
    lastName: string;
    role: UserRole;
}

export enum UserRole {
    ADMIN = "ADMIN",
    CA_USER = "CA_USER",
    USER = "USER",
}