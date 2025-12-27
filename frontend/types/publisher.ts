export type Publisher = {
    publisherId: number;
    name: string;
    address?: string | null;
    phone?: string | null;
};

export type CreatePublisherPayload = {
    name: string;
    address?: string | null;
    phone?: string | null;
};

export type UpdatePublisherPayload = {
    name: string;
    address?: string | null;
    phone?: string | null;
};

