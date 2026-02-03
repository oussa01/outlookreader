export interface LoginCredentials {
    type:string;
    loginUrlPath:string;
    generatedCode:string;
    expiration : number;
}

export interface AppSession {
    type: string;
    sessionToken: string;
    isAuthenticated: boolean;
}